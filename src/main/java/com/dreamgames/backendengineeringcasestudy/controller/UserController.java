package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.repository.model.User;
import com.dreamgames.backendengineeringcasestudy.service.user.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController{
    private final UserService _userService;

    @Autowired
    public UserController(UserService userService){
        _userService = userService;
    }
    //example
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestParam String username){
        User newUser = _userService.createUser(username);
        return ResponseEntity.ok(newUser);
    }
}
