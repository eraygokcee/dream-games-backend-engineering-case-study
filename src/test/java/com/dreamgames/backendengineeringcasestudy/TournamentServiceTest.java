package com.dreamgames.backendengineeringcasestudy;

import com.dreamgames.backendengineeringcasestudy.exceptions.EnterTournamentExceptions;
import com.dreamgames.backendengineeringcasestudy.tournament.helper.TournamentHelper;
import com.dreamgames.backendengineeringcasestudy.tournament.model.Tournament;
import com.dreamgames.backendengineeringcasestudy.tournament.model.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentParticipationRepository;
import com.dreamgames.backendengineeringcasestudy.tournament.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.users.model.User;
import com.dreamgames.backendengineeringcasestudy.users.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.tournament.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TournamentParticipationRepository tournamentParticipationRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TournamentHelper tournamentHelper;
    @Mock
    private RedisTemplate<String, TournamentGroup> groupRedisTemplate;

    @InjectMocks
    private TournamentService tournamentService;

    @Test
    public void whenUserNotFoundThenThrowUserNotFoundException() {
        when(userRepository.findById(any(String.class)))
                .thenThrow(new EnterTournamentExceptions.UserNotFoundException("User not found"));

        assertThrows(EnterTournamentExceptions.UserNotFoundException.class, () -> {
            tournamentService.enterTournament("1");
        });
    }

    @Test
    public void whenUserHasUnclaimedRewardThenThrowException(){
        User userReq = generateUserModel("1","Turkey",20,5000);
        when(userRepository.findById(any(String.class)))
                .thenReturn(Optional.of(userReq));
        when(tournamentParticipationRepository.findRewardEligibleByUserId(userReq.getId()))
                .thenThrow(new EnterTournamentExceptions.UnclaimedRewardException("Exception"));

        assertThrows(EnterTournamentExceptions.UnclaimedRewardException.class,()->{
            tournamentService.enterTournament(userReq.getId());
        });
    }

    @Test
    public void whenNoActiveTournamentReturnExpectedException(){
        User userReq = generateUserModel("1","Turkey",20,5000);
        when(userRepository.findById(any(String.class)))
                .thenReturn(Optional.of(userReq));
        when(tournamentParticipationRepository.findRewardEligibleByUserId(userReq.getId()))
                .thenReturn(null);
        when(tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE"))
                .thenThrow(new EnterTournamentExceptions.TournamentNotFoundException("No active tournament available"));

        assertThrows(EnterTournamentExceptions.TournamentNotFoundException.class,()->{
            tournamentService.enterTournament(userReq.getId());
        });
    }

    @Test
    public void whenExistParticipationInTournamentReturnExpectedException(){
        User userReq = generateUserModel("1","Turkey",20,5000);
        Tournament tournament = generateTournamentModel("2","ACTIVE");
        when(userRepository.findById(any(String.class)))
                .thenReturn(Optional.of(userReq));
        when(tournamentParticipationRepository.findRewardEligibleByUserId(userReq.getId()))
                .thenReturn(null);
        when(tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE"))
                .thenReturn(Optional.of(tournament));
        when(tournamentParticipationRepository.existsByUserIdAndTournamentId(userReq.getId(),"2"))
                .thenThrow(new EnterTournamentExceptions.HasAlreadyEnteredTournamentException("User has already entered this tournament."));

        assertThrows(EnterTournamentExceptions.HasAlreadyEnteredTournamentException.class,()-> {
            tournamentService.enterTournament(userReq.getId());
        });
    }

    @Test
    public void whenUserLevelIsLowerThan20ReturnExpectedException(){
        User userReq = generateUserModel("1","Turkey",5,5000);
        Tournament tournament = generateTournamentModel("2","ACTIVE");
        when(userRepository.findById(any(String.class)))
                .thenReturn(Optional.of(userReq));
        when(tournamentParticipationRepository.findRewardEligibleByUserId(userReq.getId()))
                .thenReturn(null);
        when(tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE"))
                .thenReturn(Optional.of(tournament));
        when(tournamentParticipationRepository.existsByUserIdAndTournamentId(userReq.getId(),"2"))
                .thenReturn(false);
        assertThrows(EnterTournamentExceptions.LowLevelException.class,()->{
           tournamentService.enterTournament(userReq.getId()) ;
        });
    }
    @Test
    public void whenUserCoinsIsLowerThan1000ReturnExpectedException(){
        User userReq = generateUserModel("1","Turkey",21,50);
        Tournament tournament = generateTournamentModel("2","ACTIVE");
        when(userRepository.findById(any(String.class)))
                .thenReturn(Optional.of(userReq));
        when(tournamentParticipationRepository.findRewardEligibleByUserId(userReq.getId()))
                .thenReturn(null);
        when(tournamentRepository.findFirstByStatusOrderByStartTimeDesc("ACTIVE"))
                .thenReturn(Optional.of(tournament));
        when(tournamentParticipationRepository.existsByUserIdAndTournamentId(userReq.getId(),"2"))
                .thenReturn(false);
        assertThrows(EnterTournamentExceptions.LowBalanceException.class,()->{
            tournamentService.enterTournament(userReq.getId()) ;
        });
    }

    @Test
    public void whenCreateTournamentReturnExpectedTournament() {
        Tournament tournament = generateTournamentModel("2", "ACTIVE");
        when(tournamentHelper.createTournament())
                .thenReturn(tournament);
        Tournament response = tournamentService.createTournament();

        assertEquals(response,tournament);
    }
    @Test
    public void whenEndTournamentReturnExpectedTournament() {
        String message = "Tournament has been end.";
        when(tournamentHelper.endTournament())
                .thenReturn(message);
        String response = tournamentService.endTournament();

        assertEquals(response,message);
    }

    private User generateUserModel(String id, String country, int level, int coins) {
        User user = new User();
        user.setId(id);
        user.setCountry(country);
        user.setLevel(level);
        user.setCoins(coins);
        return user;
    }

    private Tournament generateTournamentModel(String id, String status) {
        Tournament tournament = new Tournament();
        tournament.setId(id);
        tournament.setStatus(status);
        return tournament;
    }
}
