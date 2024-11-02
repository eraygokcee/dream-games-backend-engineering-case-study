package com.dreamgames.backendengineeringcasestudy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EnterTournamentExceptions.UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(EnterTournamentExceptions.UserNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EnterTournamentExceptions.TournamentNotFoundException.class)
    public ResponseEntity<?> handleTournamentNotFoundException(EnterTournamentExceptions.TournamentNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EnterTournamentExceptions.UnclaimedRewardException.class)
    public ResponseEntity<?> handleUnclaimedRewardException(EnterTournamentExceptions.UnclaimedRewardException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(EnterTournamentExceptions.HasAlreadyEnteredTournamentException.class)
    public ResponseEntity<?> handleHasAlreadyEnteredTournamentException(EnterTournamentExceptions.HasAlreadyEnteredTournamentException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(EnterTournamentExceptions.LowLevelException.class)
    public ResponseEntity<?> handleHasAlreadyEnteredTournamentException(EnterTournamentExceptions.LowLevelException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EnterTournamentExceptions.LowBalanceException.class)
    public ResponseEntity<?> handleHasAlreadyEnteredTournamentException(EnterTournamentExceptions.LowBalanceException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(ClaimRewardExceptions.NoClaimRewardException.class)
    public ResponseEntity<?> handleHasAlreadyEnteredTournamentException(ClaimRewardExceptions.NoClaimRewardException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(GetGroupLeaderboardExceptions.GroupNotFoundException.class)
    public ResponseEntity<?> handleGroupNotFound(GetGroupLeaderboardExceptions.GroupNotFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(GetGroupLeaderboardExceptions.GroupParticipationsNotFoundException.class)
    public ResponseEntity<?> handleHasAlreadyEnteredTournamentException(GetGroupLeaderboardExceptions.GroupParticipationsNotFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
