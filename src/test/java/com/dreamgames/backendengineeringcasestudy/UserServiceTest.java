package com.dreamgames.backendengineeringcasestudy;

import com.dreamgames.backendengineeringcasestudy.exceptions.EnterTournamentExceptions;
import com.dreamgames.backendengineeringcasestudy.users.helper.UserHelper;
import com.dreamgames.backendengineeringcasestudy.users.model.User;
import com.dreamgames.backendengineeringcasestudy.users.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.users.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserHelper userHelper;
    @InjectMocks
    private UserService userService;


    @Test
    public void testCreateUserReturnExpectedModel(){

        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        when(userHelper.randomCountry()).thenReturn("Turkey");

        User response = userService.createUser();

        assertEquals(1,response.getLevel());
        assertEquals(5000,response.getCoins());
        assertInstanceOf(User.class,response);
    }

    @Test
    public void whenUserNotFoundReturnExpectedException() {

        when(userRepository.findById(any(String.class))).thenThrow(new EnterTournamentExceptions.UserNotFoundException("User not found"));

        Assertions.assertThrows(EnterTournamentExceptions.UserNotFoundException.class, () -> {
            userService.updateLevel("1");
        });
    }

    @Test
    public void whenUserFoundReturnExceptedUpdatedLevelData(){
        User dbUser = GenerateUserModel("1", "Turkey", 1, 5000);
        when(userRepository.findById("1"))
                .thenReturn(Optional.of(dbUser));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User responseService = userService.updateLevel("1");

        assertEquals(responseService.getLevel(),2);
        assertEquals(responseService.getCoins(),5025);
    }

    @Test
    public void whenUserFoundReturnExpectedUser(){
        User dbUser = GenerateUserModel("1", "Turkey", 1, 5000);
        when(userRepository.findById("1"))
                .thenReturn(Optional.of(dbUser));

        User responseService = userService.getUser("1");

        assertEquals(responseService,dbUser);
    }

    //GenerateMethods
    private User GenerateUserModel(String id,String country,int level,int coins){
        User newUser = new User();
        newUser.setId(id);
        newUser.setCountry(country);
        newUser.setLevel(level);
        newUser.setCoins(coins);
        return newUser;
    }
}
