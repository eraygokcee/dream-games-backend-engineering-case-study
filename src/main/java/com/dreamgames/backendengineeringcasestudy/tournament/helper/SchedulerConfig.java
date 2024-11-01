package com.dreamgames.backendengineeringcasestudy.tournament.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private TournamentHelper tournamentHelper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void startTournament() {
        tournamentHelper.createTournament();
    }

    @Scheduled(cron = "0 0 20 * * ?")
    public void endTournament() {
        tournamentHelper.endTournament();
    }
}