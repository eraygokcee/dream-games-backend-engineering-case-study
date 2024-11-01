package com.dreamgames.backendengineeringcasestudy.tournament.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final RedisTemplate<String, String> customStringRedisTemplate;



    @Autowired
    public TournamentService(
            TournamentParticipationRepository tournamentParticipationRepository,
            TournamentRepository tournamentRepository,
            TournamentHelper tournamentHelper,
            UserRepository userRepository,
            @Qualifier("groupRedisTemplate") RedisTemplate<String, TournamentGroup> groupRedisTemplate,
            @Qualifier("customStringRedisTemplate") RedisTemplate<String, String> customStringRedisTemplate) {
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentHelper = tournamentHelper;
        this.userRepository = userRepository;
        this.groupRedisTemplate = groupRedisTemplate;
        this.customStringRedisTemplate = customStringRedisTemplate;
    }

    private static final String GROUP_ASSIGNMENTS_KEY = "groupAssignments:";
    private static final String TOURNAMENT_GROUPS_KEY = "tournamentGroups:";

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    public void createDailyTournament() {
        tournamentHelper.createDailyTournament();
    }

    public ResponseEntity<?> enterTournament(String userId) {

        try{
            Map<String, Object> response = new HashMap<>();

            User user = userRepository.findById(userId)
                    .orElse(null);
            if(user == null){
                response.put("message","User not found");
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
            Integer hasUnclaimedReward = tournamentParticipationRepository.findRewardEligibleByUserId(userId);

            if (hasUnclaimedReward != null ) {
                response.put("message", "You have an unclaimed reward from a previous tournament. Please claim your reward before entering a new tournament.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            Tournament activeTournament = tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE")
                    .orElse(null);
            if(activeTournament == null){
                response.put("message","No active tournament available");
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }

            boolean existingParticipation = tournamentParticipationRepository
                    .existsByUserIdAndTournamentId(userId, activeTournament.getId());

            if (existingParticipation) {
                response.put("message","User has already entered this tournament");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (user.getLevel() < 20) {
                response.put("message","User must be at least level 20 to enter the tournament");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (user.getCoins() < 1000) {
                response.put("message","User does not have enough coins to enter the tournament");
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }

            user.setCoins(user.getCoins() - 1000);
            userRepository.save(user);

            TournamentGroup group = assignUserToGroup(user, activeTournament);
            TournamentParticipation participation = new TournamentParticipation(
                    UUID.randomUUID().toString(),
                    userId,
                    activeTournament.getId(),
                    group.getId(),
                    0,
                    activeTournament);
            tournamentParticipationRepository.save(participation);
            response.put("message","User has successfully entered the tournament.");
            response.put("tournament",activeTournament);
            response.put("group",group.getId());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Tournament createTournament() {
        return tournamentHelper.createManuellyTournament();
    }
    public ResponseEntity<?> endTournament(){
        Map<String, Object> response = new HashMap<>();

        Tournament activeTournament = tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE")
                .orElse(null);
        if(activeTournament == null){
            response.put("message","No active tournament available");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        List<TournamentGroup> groups = getGroupsForTournament(activeTournament.getId());
        for (TournamentGroup group : groups) {
            // Grubun katılımcılarını alın ve sıralayın
            List<TournamentParticipation> participations = tournamentParticipationRepository.findByGroupId(group.getId());
            participations.sort(Comparator.comparingInt(TournamentParticipation::getScore).reversed());

            // İlk iki kullanıcıya ödül hakkı verin
            TournamentParticipation participation = null;
            for (int i = 0; i < participations.size(); i++) {
                participation = participations.get(i);
                if (i == 0) {
                    participation.setRewardEligible(1);
                } else if (i == 1) {
                    participation.setRewardEligible(2);
                } else {
                    break;
                }
            }
            tournamentParticipationRepository.save(participation);
        }

        // Turnuva durumunu güncelle
        activeTournament.setStatus("FINISHED");
        tournamentRepository.save(activeTournament);
        response.put("message","Tournament has been end.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public List<TournamentGroup> getGroupsForTournament(String tournamentId) {
        String tournamentGroupsKey = TOURNAMENT_GROUPS_KEY + tournamentId;

        // Turnuvaya ait grup ID'lerini alıyoruz
        Set<String> groupIds = customStringRedisTemplate.opsForSet().members(tournamentGroupsKey);
        List<TournamentGroup> groups = new ArrayList<>();

        if (groupIds != null && !groupIds.isEmpty()) {
            // Grupları Redis'ten alıyoruz
            for (String groupId : groupIds) {
                String groupKey = GROUP_ASSIGNMENTS_KEY + groupId;
                TournamentGroup group = groupRedisTemplate.opsForValue().get(groupKey);
                if (group != null) {
                    groups.add(group);
                }
            }
        }

        return groups;
    }
    @Transactional
    public String claimReward(String userId) {
        Integer hasUnclaimedReward = tournamentParticipationRepository.findRewardEligibleByUserId(userId);

        if (hasUnclaimedReward == null) {
            return "No rewards to claim.";
        }

        if(hasUnclaimedReward == 1 ){
            userRepository.findById(userId).ifPresent(user -> {
                user.setCoins(user.getCoins() + 10000);
                userRepository.save(user);
            });
            tournamentParticipationRepository.updateRewardEligibleByUserId(userId);
            return "You have won 10,000 coins!";
        }else{
            userRepository.findById(userId).ifPresent(user -> {
                user.setCoins(user.getCoins() + 5000);
                userRepository.save(user);
            });
            tournamentParticipationRepository.updateRewardEligibleByUserId(userId);
            return "You have won 5.000 coins";
        }
    }

    public TournamentGroup assignUserToGroup(User user, Tournament tournament) {
        String tournamentGroupsKey = TOURNAMENT_GROUPS_KEY + tournament.getId();

        // İlgili turnuvanın grup ID'lerini alıyoruz
        Set<String> groupIds = customStringRedisTemplate.opsForSet().members(tournamentGroupsKey);
        List<TournamentGroup> groups = new ArrayList<>();

        if (groupIds != null && !groupIds.isEmpty()) {
            // Grupları Redis'ten alıyoruz
            for (String groupId : groupIds) {
                String groupKey = GROUP_ASSIGNMENTS_KEY + groupId;
                TournamentGroup group = groupRedisTemplate.opsForValue().get(groupKey);
                if (group != null) {
                    groups.add(group);
                }
            }
        }

        // Uygun bir grup buluyoruz
        TournamentGroup group = groups.stream()
                .filter(g -> g.getUsers().size() < 5
                        && !g.getUsers().containsValue(user.getCountry())
                        && g.getTournamentId().equals(tournament.getId()))
                .findFirst()
                .orElseGet(() -> {
                    // Yeni bir grup oluşturuyoruz
                    TournamentGroup newGroup = new TournamentGroup(UUID.randomUUID().toString(), tournament.getId());
                    saveGroupAssignments(newGroup.getId(), newGroup);
                    addGroupIdToTournament(tournament.getId(), newGroup.getId());
                    return newGroup;
                });

        // Kullanıcıyı gruba ekliyoruz
        group.addUser(user.getId(), user.getCountry());
        saveGroupAssignments(group.getId(), group);
        return group;
    }

    public void saveGroupAssignments(String groupId, TournamentGroup group) {
        String key = GROUP_ASSIGNMENTS_KEY + groupId;
        groupRedisTemplate.opsForValue().set(key, group);
    }

    public void addGroupIdToTournament(String tournamentId, String groupId) {
        String key = TOURNAMENT_GROUPS_KEY + tournamentId;
        customStringRedisTemplate.opsForSet().add(key, groupId);
    }

    public Map<String, Object> getGroupLeaderboard(String groupId) {
        String key = GROUP_ASSIGNMENTS_KEY + groupId;
        TournamentGroup group = groupRedisTemplate.opsForValue().get(key);

        if (group == null) {
            throw new RuntimeException("Group not found");
        }

        List<TournamentParticipation> groupParticipations = tournamentParticipationRepository.findByGroupId(groupId);

        groupParticipations.sort(Comparator.comparingInt(TournamentParticipation::getScore).reversed());

        List<Map<String, Object>> rankings = groupParticipations.stream()
                .map(participation -> {
                    User user = userRepository.findById(participation.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", user.getId());
                    userInfo.put("country", user.getCountry());
                    userInfo.put("tournamentScore", participation.getScore());
                    return userInfo;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("groupId", groupId);
        response.put("rankings", rankings);
        return response;
    }

    public Map<String, Integer> getCountryLeaderboard(String tournamentId) {
        List<TournamentParticipation> tournamentParticipations = tournamentParticipationRepository.findByTournamentId(tournamentId);

        Map<String, Integer> countryScores = tournamentParticipations.stream()
                .collect(Collectors.groupingBy(
                        participation -> userRepository.findById(participation.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found")).getCountry(),
                        Collectors.summingInt(TournamentParticipation::getScore)
                ));

        return countryScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


}
