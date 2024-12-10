package com.kugan.spring_basic.controller;

import com.kugan.spring_basic.entity.User;
import com.kugan.spring_basic.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class userController {

    private UserRepository userRepository;

    public userController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getUser")
    public ResponseEntity<List<User>> getAllUser (){
        List<User> results= userRepository.findAll();
        return ResponseEntity.ok().body(results);
    }
}
