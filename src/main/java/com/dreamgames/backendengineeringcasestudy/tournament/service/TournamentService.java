package com.dreamgames.backendengineeringcasestudy.tournament.service;

import com.dreamgames.backendengineeringcasestudy.tournament.helper.TournamentHelper;
import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentParticipation;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentParticipationRepository;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.users.model.User;
import com.dreamgames.backendengineeringcasestudy.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    private final TournamentGroupRepository tournamentGroupRepository;
    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentHelper tournamentHelper;
    private final UserRepository userRepository;

    @Autowired
    public TournamentService(TournamentGroupRepository tournamentGroupRepository, TournamentParticipationRepository tournamentParticipationRepository, TournamentRepository tournamentRepository, TournamentHelper tournamentHelper, UserRepository userRepository) {
        this.tournamentGroupRepository = tournamentGroupRepository;
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentHelper = tournamentHelper;
        this.userRepository = userRepository;
    }

    private final Map<String, TournamentGroup> groupAssignments = new HashMap<>();

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    public void createDailyTournament(){
        tournamentHelper.createDailyTournament();
    }

    public Tournament enterTournament(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getLevel() < 20) {
            throw new RuntimeException("User must be at least level 20 to enter the tournament");
        }
        if (user.getCoins() < 1000) {
            throw new RuntimeException("User does not have enough coins to enter the tournament");
        }
        Tournament activeTournament = tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE")
                .orElseThrow(() -> new RuntimeException("No active tournament available"));
        user.setCoins(user.getCoins() - 1000);
        userRepository.save(user);

        TournamentGroup group = assignUserToGroup(user, activeTournament);
        TournamentParticipation participation = new TournamentParticipation(UUID.randomUUID().toString(), userId, activeTournament.getId(), group.getId(), 0,activeTournament);
        tournamentParticipationRepository.save(participation);
        return activeTournament;
    }

    public Tournament createTournament(){
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID().toString());
        tournament.setStartTime(LocalDateTime.now());
        tournament.setEndTime(LocalDateTime.now().plusHours(20));
        tournament.setStatus("ACTIVE");
        tournamentRepository.save(tournament);
        return tournament;
    }

    public String claimReward(String userId) {
        List<TournamentParticipation> finishedParticipations = tournamentParticipationRepository.findByUserIdAndTournamentStatus(userId, "FINISHED");

        if (finishedParticipations.isEmpty()) {
            throw new RuntimeException("No finished tournament to claim rewards from");
        }

        TournamentParticipation participation = finishedParticipations.get(0); // Ödülünü almadığı ilk bitmiş turnuva
        List<TournamentParticipation> groupParticipations = tournamentParticipationRepository.findByGroupId(participation.getGroupId());

        groupParticipations.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        int rank = 1;
        for (TournamentParticipation p : groupParticipations) {
            if (p.getUserId().equals(userId)) {
                break;
            }
            rank++;
        }

        if (rank == 1) {
            userRepository.findById(userId).ifPresent(user -> {
                user.setCoins(user.getCoins() + 10000);
                userRepository.save(user);
            });
            return "You have won 10,000 coins!";
        } else if (rank == 2) {
            userRepository.findById(userId).ifPresent(user -> {
                user.setCoins(user.getCoins() + 5000);
                userRepository.save(user);
            });
            return "You have won 5,000 coins!";
        }

        return "No reward available.";
    }


    public TournamentGroup assignUserToGroup(User user, Tournament tournament) {
        TournamentGroup group = groupAssignments.values().stream()
                .filter(g -> g.getUsers().size() < 5 && !g.getUsers().containsValue(user.getCountry()) && g.getTournamentId().equals(tournament.getId()))
                .findFirst().orElseGet(() -> {
                    TournamentGroup newGroup = new TournamentGroup(UUID.randomUUID().toString(), tournament.getId());
                    groupAssignments.put(newGroup.getId(), newGroup);
                    tournamentGroupRepository.save(newGroup);
                    return newGroup;
                });
        group.addUser(user.getId(), user.getCountry());
        saveGroupAssignments(group.getId(), group);
        return group;
    }

    public void saveGroupAssignments(String groupId, TournamentGroup group) {
        groupAssignments.put(groupId, group);
    }

    public Map<String, Object> getGroupLeaderboard(String groupId) {
        List<TournamentParticipation> groupParticipations = tournamentParticipationRepository.findByGroupId(groupId);

        groupParticipations.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        List<Map<String, Object>> rankings = groupParticipations.stream()
                .map(participation -> {
                    User user = userRepository.findById(participation.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", user.getId());
                    userInfo.put("username", user.getId());  // Kullanıcı adı yerine kimlik kullanılıyor
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

    public Map<String, Integer> getCountryLeaderboard() {
        List<TournamentParticipation> allParticipations = tournamentParticipationRepository.findAll();

        Map<String, Integer> countryScores = allParticipations.stream()
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
