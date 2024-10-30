package com.dreamgames.backendengineeringcasestudy.tournament.repository;

import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentParticipationRepository extends JpaRepository<TournamentParticipation, String> {
    List<TournamentParticipation> findByUserIdAndTournamentStatus(String userId, String status);
    TournamentParticipation findByUserIdAndGroupId(String userId, String groupId);
    List<TournamentParticipation> findByGroupId(String groupId);

}