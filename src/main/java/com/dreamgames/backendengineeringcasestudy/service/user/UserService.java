package com.dreamgames.backendengineeringcasestudy.service.user;

import com.dreamgames.backendengineeringcasestudy.repository.core.UserRepository;
import com.dreamgames.backendengineeringcasestudy.repository.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
//example
@Service
public class UserService {
    private final UserRepository _userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        _userRepository = userRepository;
    }

    public User createUser(String username){
        List<String> countries = List.of("Turkey","United States","United Kingdom", "France" , "Germany");
        String assignedCountry = countries.get(new Random().nextInt(countries.size()));

        User user = new User();
        user.setUsername(username);
        user.setCountry(assignedCountry);
        user.setLevel(1);
        user.setCoins(5000);

        return _userRepository.save(user);
    }
}
