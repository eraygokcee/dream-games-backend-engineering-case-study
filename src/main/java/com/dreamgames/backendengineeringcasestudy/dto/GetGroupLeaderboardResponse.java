package com.dreamgames.backendengineeringcasestudy.dto;

import java.util.List;
import java.util.Map;

public class GetGroupLeaderboardResponse {
    private List<Map<String, Object>> Rankings;
    private String GroupId;


    public GetGroupLeaderboardResponse() {
    }

    public List<Map<String, Object>> getRankings() {
        return Rankings;
    }

    public void setRankings(List<Map<String, Object>> rankings) {
        Rankings = rankings;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public GetGroupLeaderboardResponse(List<Map<String, Object>> rankings, String groupId) {
        Rankings = rankings;
        GroupId = groupId;
    }
}
