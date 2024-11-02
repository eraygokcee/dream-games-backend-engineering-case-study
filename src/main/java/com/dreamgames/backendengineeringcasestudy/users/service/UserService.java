package com.dreamgames.backendengineeringcasestudy.users.service;

import com.dreamgames.backendengineeringcasestudy.exceptions.EnterTournamentExceptions;
import com.dreamgames.backendengineeringcasestudy.users.helper.UserHelper;
import com.dreamgames.backendengineeringcasestudy.users.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

//example
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserHelper userHelper;

    @Autowired
    public UserService(UserHelper userHelper, UserRepository userRepository) {
        this.userHelper = userHelper;
        this.userRepository = userRepository;
    }
    public User createUser(){
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setCoins(5000);
        user.setLevel(1);
        user.setCountry(userHelper.randomCountry());
        return userRepository.save(user);
    }
    public User updateLevel(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new EnterTournamentExceptions.UserNotFoundException("User Not Found"));
        user.setLevel(user.getLevel() + 1);
        user.setCoins(user.getCoins() + 25);
        return userRepository.save(user);
    }
    public User getUser(String userId){
        return userRepository.findById(userId).orElseThrow(() -> new EnterTournamentExceptions.UserNotFoundException("User Not Found"));
    }

}
