package com.dreamgames.backendengineeringcasestudy.tournament.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tournament_participation")
public class TournamentParticipation {
    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Tournament tournament;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "tournament_id")
    private String tournamentId;

    @Column(name = "group_id")
    private String groupId;

    private int score;
    private int rewardEligible; //0 no reward , 1 first rank 2 second rank


    public TournamentParticipation() {}


    public TournamentParticipation(String id, String userId, String tournamentId, String groupId, int score,Tournament tournament) {
        this.id = id;
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.groupId = groupId;
        this.score = score;
        this.tournament = tournament;
    }
    public int getRewardEligible() {
        return rewardEligible;
    }

    public void setRewardEligible(int rewardEligible) {
        this.rewardEligible = rewardEligible;
    }
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }


}
