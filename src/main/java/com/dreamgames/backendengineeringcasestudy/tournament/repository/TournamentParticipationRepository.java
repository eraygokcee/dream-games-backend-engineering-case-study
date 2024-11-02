package com.dreamgames.backendengineeringcasestudy.tournament.repository;

import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TournamentParticipationRepository extends JpaRepository<TournamentParticipation, String> {
    List<TournamentParticipation> findByUserIdAndTournamentStatus(String userId, String status);
    TournamentParticipation findByUserIdAndGroupId(String userId, String groupId);
    List<TournamentParticipation> findByGroupId(String groupId);
    boolean existsByUserIdAndTournamentId(String userId, String tournamentId);
    List<TournamentParticipation>  findByTournamentId(String tournamentId);
    TournamentParticipation findByUserIdAndTournamentId(String userId,String tournamentId);

    @Query("SELECT tp.rewardEligible FROM TournamentParticipation tp WHERE tp.userId = :userId AND tp.rewardEligible > 0")
    Integer findRewardEligibleByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE TournamentParticipation tp SET tp.rewardEligible = 0 WHERE tp.userId =:userId AND tp.rewardEligible > 0 ")
    int updateRewardEligibleByUserId(@Param("userId") String userId);
}