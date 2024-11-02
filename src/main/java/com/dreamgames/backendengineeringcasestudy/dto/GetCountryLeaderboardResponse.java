package com.dreamgames.backendengineeringcasestudy.dto;

import java.util.Map;

public class GetCountryLeaderboardResponse {
    private Map<String, Integer> Ranking;

    public GetCountryLeaderboardResponse(Map<String, Integer> ranking) {
        Ranking = ranking;
    }

    public GetCountryLeaderboardResponse() {
    }

    public Map<String, Integer> getData() {
        return Ranking;
    }

    public void setData(Map<String, Integer> ranking) {
        Ranking = ranking;
    }
}
