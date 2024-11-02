package com.dreamgames.backendengineeringcasestudy.exceptions;

public class ClaimRewardExceptions {

    public static class NoClaimRewardException extends RuntimeException{
        public NoClaimRewardException(String message){
            super(message);
        }
    }
}

