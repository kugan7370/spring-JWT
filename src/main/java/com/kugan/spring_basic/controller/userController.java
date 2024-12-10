package com.kugan.spring_basic.controller;

import com.kugan.spring_basic.entity.User;
import com.kugan.spring_basic.repository.UserRepository;
import com.kugan.spring_basic.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class userController {

    @Value("${role.admin}")
    private String roleAdmin;

    @Value("${role.user}")
    private String roleUser;

    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public userController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/getUser")
    public ResponseEntity<List<User>> getAllUser (){
        List<User> results= userRepository.findAll();
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("/protected-route")
    public ResponseEntity<String> getProtectedData(@RequestHeader("Authorization") String token){
        if(token !=null && token.startsWith("Bearer")){
            String jwtToken =token.substring(7);

            try{
                if(jwtUtil.isTokenValid(jwtToken)){
                    String username = jwtUtil.extractUsername(jwtToken);

                    Set<String> roles =   jwtUtil.extractRoles(jwtToken);

                    if(roles.contains(roleAdmin)){
                        return ResponseEntity.ok("Welcome " + username + " Here is the " + roles + " specific data");

                    } else if (roles.contains(roleUser)) {
                        return ResponseEntity.ok("Welcome " + username + " Here is the " + roles + " specific data");

                    }
                    else{
                        return ResponseEntity.status(403).body("Access Denied: You don't have the necessary role");
                    }


                }

            } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
            }
        }

            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid");

    }
}
