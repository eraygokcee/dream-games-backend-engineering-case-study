package com.dreamgames.backendengineeringcasestudy.dto;

public class GetGroupRankResponse {
    private int Rank;

    public GetGroupRankResponse(int rank) {
        Rank = rank;
    }

    public int getRank() {
        return Rank;
    }

    public void setRank(int rank) {
        Rank = rank;
    }
}
