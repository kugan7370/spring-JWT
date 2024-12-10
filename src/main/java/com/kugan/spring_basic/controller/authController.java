package com.kugan.spring_basic.controller;

import com.kugan.spring_basic.dto.LoginRequest;
import com.kugan.spring_basic.dto.RegisterRequest;
import com.kugan.spring_basic.entity.Role;
import com.kugan.spring_basic.entity.User;
import com.kugan.spring_basic.repository.RoleRepository;
import com.kugan.spring_basic.repository.UserRepository;
import com.kugan.spring_basic.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class authController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private  final PasswordEncoder passwordEncoder;

    public authController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register (@RequestBody RegisterRequest registerRequest){
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("username is already exits");
        }

        User newUser = new User();

        newUser.setUsername(registerRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        newUser.setPassword(encodedPassword);

        Set<Role> roles = new HashSet<>();
        for(String roleName: registerRequest.getRoles()){
            Role role = roleRepository.findByName(roleName).orElseThrow(()->new RuntimeException("role not found"+ roleName));
            roles.add(role);

        }

        newUser.setRoles(roles);
        userRepository.save(newUser);
        return ResponseEntity.ok("user register successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        String token = jwtUtil.generatetoken(loginRequest.getUsername());
        return ResponseEntity.ok().body(token);
    }

}
