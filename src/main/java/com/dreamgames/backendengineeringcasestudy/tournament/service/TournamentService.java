package com.dreamgames.backendengineeringcasestudy.tournament.service;

import com.dreamgames.backendengineeringcasestudy.dto.*;
import com.dreamgames.backendengineeringcasestudy.exceptions.ClaimRewardExceptions;
import com.dreamgames.backendengineeringcasestudy.exceptions.EnterTournamentExceptions;
import com.dreamgames.backendengineeringcasestudy.exceptions.GetGroupLeaderboardExceptions;
import com.dreamgames.backendengineeringcasestudy.tournament.helper.TournamentHelper;
import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentParticipation;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentParticipationRepository;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.users.model.User;
import com.dreamgames.backendengineeringcasestudy.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentHelper tournamentHelper;
    private final UserRepository userRepository;
    private final RedisTemplate<String, TournamentGroup> groupRedisTemplate;

    @Autowired
    public TournamentService(
            TournamentParticipationRepository tournamentParticipationRepository,
            TournamentRepository tournamentRepository,
            TournamentHelper tournamentHelper,
            UserRepository userRepository,
            @Qualifier("groupRedisTemplate") RedisTemplate<String, TournamentGroup> groupRedisTemplate)
            {
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentHelper = tournamentHelper;
        this.userRepository = userRepository;
        this.groupRedisTemplate = groupRedisTemplate;
    }

    public EnterTournamentResponse enterTournament(String userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EnterTournamentExceptions.UserNotFoundException("User not found"));

            Integer hasUnclaimedReward = tournamentParticipationRepository.findRewardEligibleByUserId(userId);

            if (hasUnclaimedReward != null ) {
                throw new EnterTournamentExceptions.UnclaimedRewardException("You have an unclaimed reward from a previous tournament. Please claim your reward before entering a new tournament.");
            }
            Tournament activeTournament = tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE")
                    .orElseThrow(()->new EnterTournamentExceptions.TournamentNotFoundException("No active tournament available"));

            boolean existingParticipation = tournamentParticipationRepository
                    .existsByUserIdAndTournamentId(userId, activeTournament.getId());
            if (existingParticipation) {
                throw new EnterTournamentExceptions.HasAlreadyEnteredTournamentException("User has already entered this tournament.");
            }
            if (user.getLevel() < 20) {
                throw new EnterTournamentExceptions.LowLevelException("User must be at least level 20 to enter the tournament");
            }
            if (user.getCoins() < 1000) {
                throw new EnterTournamentExceptions.LowBalanceException("User does not have enough coins to enter the tournament");
            }
            user.setCoins(user.getCoins() - 1000);
            userRepository.save(user);

            TournamentGroup group = tournamentHelper.assignUserToGroup(user, activeTournament);
            TournamentParticipation participation = new TournamentParticipation(
                    UUID.randomUUID().toString(),
                    userId,
                    activeTournament.getId(),
                    group.getId(),
                    0,
                    activeTournament);
            tournamentParticipationRepository.save(participation);

            EnterTournamentResponse response = new EnterTournamentResponse();
            response.setTournament(activeTournament);
            response.setMessage("User has successfully entered the tournament.");
            response.setGroupLeaderboard(getGroupLeaderboard(group.getId()));
            return response;
    }
    public Tournament createTournament() {
        return tournamentHelper.createTournament();
    }
    public String endTournament(){
        return tournamentHelper.endTournament();
    }

    @Transactional
    public ClaimRewardResponse claimReward(String userId) {
        Integer hasUnclaimedReward = tournamentParticipationRepository.findRewardEligibleByUserId(userId);

        if (hasUnclaimedReward == null) {
            throw new ClaimRewardExceptions.NoClaimRewardException("There is no reward to be won");
        }
        ClaimRewardResponse response = new ClaimRewardResponse();
        if(hasUnclaimedReward == 1 ){
            userRepository.findById(userId).ifPresent(user -> {
                user.setCoins(user.getCoins() + 10000);
                userRepository.save(user);
                response.setMessage("You have won 10,000 coins!");
                response.setUser(user);
            });
            tournamentParticipationRepository.updateRewardEligibleByUserId(userId);
        }else{
            userRepository.findById(userId).ifPresent(user -> {
                user.setCoins(user.getCoins() + 5000);
                userRepository.save(user);
                response.setMessage("You have won 5.000 coins");
                response.setUser(user);
            });
            tournamentParticipationRepository.updateRewardEligibleByUserId(userId);
        }
        return response;
    }


    private static final String GROUP_ASSIGNMENTS_KEY = "groupAssignments:";
    public GetGroupLeaderboardResponse getGroupLeaderboard(String groupId) {
        String key = GROUP_ASSIGNMENTS_KEY + groupId;
        TournamentGroup group = groupRedisTemplate.opsForValue().get(key);

        if (group == null) {
            throw new GetGroupLeaderboardExceptions.GroupNotFoundException("Group Not Found");
        }

        List<TournamentParticipation> groupParticipations = tournamentParticipationRepository.findByGroupId(groupId);

        if(groupParticipations == null){
            throw new GetGroupLeaderboardExceptions.GroupParticipationsNotFoundException("Group participations not found");
        }

        groupParticipations.sort(Comparator.comparingInt(TournamentParticipation::getScore).reversed());

        List<Map<String, Object>> rankings = groupParticipations.stream()
                .map(participation -> {
                    User user = userRepository.findById(participation.getUserId())
                            .orElseThrow(() -> new EnterTournamentExceptions.UserNotFoundException("User not found"));
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", user.getId());
                    userInfo.put("country", user.getCountry());
                    userInfo.put("tournamentScore", participation.getScore());
                    return userInfo;
                })
                .collect(Collectors.toList());

        return new GetGroupLeaderboardResponse(rankings,groupId);
    }

    public GetCountryLeaderboardResponse getCountryLeaderboard(String tournamentId) {
        List<TournamentParticipation> tournamentParticipations = tournamentParticipationRepository.findByTournamentId(tournamentId);

        if(tournamentParticipations == null){
            throw new GetGroupLeaderboardExceptions.GroupParticipationsNotFoundException("Group participations not found");
        }

        Map<String, Integer> countryScores = tournamentParticipations.stream()
                .collect(Collectors.groupingBy(
                        participation -> userRepository.findById(participation.getUserId())
                                .orElseThrow(() -> new EnterTournamentExceptions.UserNotFoundException("User Not Found")).getCountry(),
                        Collectors.summingInt(TournamentParticipation::getScore)
                ));

        Map<String,Integer> sortedCountryScores = countryScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        return new GetCountryLeaderboardResponse(sortedCountryScores);
    }
    public GetGroupRankResponse getGroupRank(String userId,String tournamentId){
        TournamentParticipation participation = tournamentParticipationRepository.findByUserIdAndTournamentId(userId,tournamentId);

        List<Map<String, Object>> getGroupLeaderboardResponse = getGroupLeaderboard(participation.getGroupId()).getRankings();
        int rank= 1;
        for(Map<String,Object> r:getGroupLeaderboardResponse){
            if(r.get("userId").equals(userId)){
                break;
            }
            rank++;
        }
        return new GetGroupRankResponse(rank);
    }
}
