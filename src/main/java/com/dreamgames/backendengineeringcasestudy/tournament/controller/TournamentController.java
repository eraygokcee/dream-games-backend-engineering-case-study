package com.dreamgames.backendengineeringcasestudy.tournament.controller;

import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/enter/{userId}")
    public ResponseEntity<Tournament> enterTournament(@PathVariable String userId){
        Tournament tournament = tournamentService.enterTournament(userId);
        return new ResponseEntity<>(tournament, HttpStatus.OK);
    }
    @PostMapping("/")
    public ResponseEntity<Tournament> createTournament(){
        Tournament tournament = tournamentService.createTournament();
        return new ResponseEntity<>(tournament,HttpStatus.OK);
    }
    @PostMapping("/claimReward/{userId}")
    public ResponseEntity<String> claimReward(@PathVariable String userId){
        String response = tournamentService.claimReward(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/groupLeaderboard/{groupId}")
    public ResponseEntity<Map<String,Object>> getGroupLeaderboard(@PathVariable String groupId){
        Map<String,Object> response = tournamentService.getGroupLeaderboard(groupId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/countryLeaderboard")
    public ResponseEntity<Map<String, Integer>> getCountryLeaderboard() {
        Map<String, Integer> leaderboard = tournamentService.getCountryLeaderboard();
        return new ResponseEntity<>(leaderboard, HttpStatus.OK);
    }
}
