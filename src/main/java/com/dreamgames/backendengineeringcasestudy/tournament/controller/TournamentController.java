package com.dreamgames.backendengineeringcasestudy.tournament.controller;

import com.dreamgames.backendengineeringcasestudy.dto.*;
import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/enter/{userId}")
    public ResponseEntity<EnterTournamentResponse> enterTournament(@PathVariable String userId){
        EnterTournamentResponse response = tournamentService.enterTournament(userId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create")
    public ResponseEntity<Tournament> createTournament(){
        Tournament response = tournamentService.createTournament();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/end")
    public ResponseEntity<String> endTournament(){
        String response = tournamentService.endTournament();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/claimReward/{userId}")
    public ResponseEntity<ClaimRewardResponse> claimReward(@PathVariable String userId){
        ClaimRewardResponse response = tournamentService.claimReward(userId);
        return  ResponseEntity.ok(response);
    }
    @GetMapping("/getGroupRank/{userId}&{tournamentId}")
    public ResponseEntity<GetGroupRankResponse> getGroupRank(@PathVariable String userId, @PathVariable String tournamentId){
        GetGroupRankResponse response = tournamentService.getGroupRank(userId,tournamentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getGroupLeaderboard/{groupId}")
    public ResponseEntity<GetGroupLeaderboardResponse> getGroupLeaderboard(@PathVariable String groupId){
        GetGroupLeaderboardResponse response = tournamentService.getGroupLeaderboard(groupId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getCountryLeaderboard/{tournamentId}")
    public ResponseEntity<GetCountryLeaderboardResponse> getCountryLeaderboard(@PathVariable String tournamentId) {
        GetCountryLeaderboardResponse response = tournamentService.getCountryLeaderboard(tournamentId);
        return ResponseEntity.ok(response);
    }
}
