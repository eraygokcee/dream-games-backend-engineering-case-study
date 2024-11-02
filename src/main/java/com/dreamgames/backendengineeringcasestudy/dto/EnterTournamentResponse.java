package com.dreamgames.backendengineeringcasestudy.dto;

import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;

import java.util.Map;

public class EnterTournamentResponse {
    private String Message;
    private Tournament tournament;
    private GetGroupLeaderboardResponse groupLeaderboard;

    public EnterTournamentResponse(String message, GetGroupLeaderboardResponse groupLeaderboard, Tournament tournament) {
        Message = message;
        this.groupLeaderboard = groupLeaderboard;
        this.tournament = tournament;
    }

    public EnterTournamentResponse() {

    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public GetGroupLeaderboardResponse getGroupLeaderboard() {
        return groupLeaderboard;
    }

    public void setGroupLeaderboard(GetGroupLeaderboardResponse groupLeaderboard) {
        this.groupLeaderboard = groupLeaderboard;
    }
}
