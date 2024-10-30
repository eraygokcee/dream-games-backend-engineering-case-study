package com.dreamgames.backendengineeringcasestudy.users.controller;

import com.dreamgames.backendengineeringcasestudy.users.model.User;
import com.dreamgames.backendengineeringcasestudy.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController{
    @Autowired
    private UserService userService;
    //example
    @PostMapping("/create")
    public ResponseEntity<User> createUser(){
        User response = userService.createUser();
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @PutMapping("/updateLevel/{userId}")
    public ResponseEntity<User> updateLevel(@PathVariable String userId) {
        User response = userService.updateLevel(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId){
        User response= userService.getUser(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
