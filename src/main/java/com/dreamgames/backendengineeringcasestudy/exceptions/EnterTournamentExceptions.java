package com.dreamgames.backendengineeringcasestudy.exceptions;

public class EnterTournamentExceptions {
    public static class UserNotFoundException extends RuntimeException{
        public UserNotFoundException(String message){
            super(message);
        }
    }

    public static class UnclaimedRewardException extends RuntimeException{
        public UnclaimedRewardException(String message){
            super(message);
        }
    }
    public static class TournamentNotFoundException extends RuntimeException{
        public TournamentNotFoundException(String message){
            super(message);
        }
    }

    public static class HasAlreadyEnteredTournamentException extends RuntimeException{
        public HasAlreadyEnteredTournamentException(String message){
            super(message);
        }
    }
    public static class LowLevelException extends RuntimeException{
        public LowLevelException(String message){
            super(message);
        }
    }
    public static class LowBalanceException extends RuntimeException{
        public LowBalanceException(String message){
            super(message);
        }
    }

}
