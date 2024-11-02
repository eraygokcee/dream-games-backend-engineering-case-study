package com.dreamgames.backendengineeringcasestudy.exceptions;

public class GetGroupLeaderboardExceptions {

    public static class GroupNotFoundException extends RuntimeException{
        public GroupNotFoundException(String message){
            super(message);
        }
    }
    public static class GroupParticipationsNotFoundException extends RuntimeException{
        public GroupParticipationsNotFoundException(String message){
            super(message);
        }
    }
}