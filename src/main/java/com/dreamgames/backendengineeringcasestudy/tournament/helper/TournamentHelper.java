package com.dreamgames.backendengineeringcasestudy.tournament.helper;

import com.dreamgames.backendengineeringcasestudy.exceptions.EnterTournamentExceptions;
import com.dreamgames.backendengineeringcasestudy.exceptions.GetGroupLeaderboardExceptions;
import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentParticipation;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentParticipationRepository;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class TournamentHelper {

    private final TournamentRepository tournamentRepository;
    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final RedisTemplate<String, TournamentGroup> groupRedisTemplate;
    private final RedisTemplate<String, String> customStringRedisTemplate;

    @Autowired public TournamentHelper(
            TournamentRepository tournamentRepository,
            TournamentParticipationRepository tournamentParticipationRepository,
            @Qualifier("groupRedisTemplate") RedisTemplate<String, TournamentGroup> groupRedisTemplate,
            @Qualifier("customStringRedisTemplate") RedisTemplate<String, String> customStringRedisTemplate)
    {
        this.tournamentRepository = tournamentRepository;
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.groupRedisTemplate = groupRedisTemplate;
        this.customStringRedisTemplate = customStringRedisTemplate;
    }

    private static final String GROUP_ASSIGNMENTS_KEY = "groupAssignments:";
    private static final String TOURNAMENT_GROUPS_KEY = "tournamentGroups:";



    public Tournament createTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID().toString());
        tournament.setStartTime(LocalDateTime.now());
        tournament.setEndTime(LocalDateTime.now().plusHours(20));
        tournament.setStatus("ACTIVE");
        tournamentRepository.save(tournament);
        return tournament;
    }

    public String endTournament(){
        Map<String, Object> response = new HashMap<>();

        Tournament activeTournament = tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE")
                .orElseThrow(()->new EnterTournamentExceptions.TournamentNotFoundException("No active tournament available"));

        //Ödül dağıt
        List<TournamentGroup> groups = getGroupsForTournament(activeTournament.getId());
        for (TournamentGroup group : groups) {
            List<TournamentParticipation> participations = tournamentParticipationRepository.findByGroupId(group.getId());

            if(participations == null){
                throw new GetGroupLeaderboardExceptions.GroupParticipationsNotFoundException("Group participation's not found");
            }
            participations.sort(Comparator.comparingInt(TournamentParticipation::getScore).reversed());

            participations.get(0).setRewardEligible(1);
            participations.get(1).setRewardEligible(2);
        }
        // Turnuva durumunu güncelle
        activeTournament.setStatus("FINISHED");
        tournamentRepository.save(activeTournament);
        return "Tournament has been end.";
    }



    public TournamentGroup assignUserToGroup(User user, Tournament tournament) {

        List<TournamentGroup> groups = getGroupsForTournament(tournament.getId());
        TournamentGroup group = groups.stream()
                .filter(g -> g.getUsers().size() < 5
                        && !g.getUsers().containsValue(user.getCountry())
                        && g.getTournamentId().equals(tournament.getId()))
                .findFirst()
                .orElseGet(() -> {
                    TournamentGroup newGroup = new TournamentGroup(UUID.randomUUID().toString(), tournament.getId());
                    saveGroupAssignments(newGroup.getId(), newGroup);
                    addGroupIdToTournament(tournament.getId(), newGroup.getId());
                    return newGroup;
                });
        group.addUser(user.getId(), user.getCountry());
        saveGroupAssignments(group.getId(), group);
        return group;
    }

    private void saveGroupAssignments(String groupId, TournamentGroup group) {
        String key = GROUP_ASSIGNMENTS_KEY + groupId;
        groupRedisTemplate.opsForValue().set(key, group);
    }

    private void addGroupIdToTournament(String tournamentId, String groupId) {
        String key = TOURNAMENT_GROUPS_KEY + tournamentId;
        customStringRedisTemplate.opsForSet().add(key, groupId);
    }

    private List<TournamentGroup> getGroupsForTournament(String tournamentId) {
        String tournamentGroupsKey = TOURNAMENT_GROUPS_KEY + tournamentId;

        Set<String> groupIds = customStringRedisTemplate.opsForSet().members(tournamentGroupsKey);
        List<TournamentGroup> groups = new ArrayList<>();

        if (groupIds != null && !groupIds.isEmpty()) {
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


}
