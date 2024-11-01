package com.dreamgames.backendengineeringcasestudy.tournament.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class TournamentGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String tournamentId;

    @ElementCollection
    private Map<String, String> users = new HashMap<>(); // UserId to Country mapping

    public TournamentGroup() {}

    public TournamentGroup(String id, String tournamentId) {
        this.id = id;
        this.tournamentId = tournamentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void addUser(String userId, String country) {
        users.put(userId, country);
    }
}
