package com.dreamgames.backendengineeringcasestudy.tournament.helper;

import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TournamentHelper {
    @Autowired
    private TournamentRepository tournamentRepository;

    public void createDailyTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID().toString());
        tournament.setStartTime(LocalDateTime.now());
        tournament.setEndTime(LocalDateTime.now().plusHours(20));
        tournament.setStatus("ACTIVE");
        tournamentRepository.save(tournament);
    }


}
