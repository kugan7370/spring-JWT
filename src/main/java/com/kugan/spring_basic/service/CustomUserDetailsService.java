package com.kugan.spring_basic.service;

import com.kugan.spring_basic.entity.User;
import com.kugan.spring_basic.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not Found" + username));

//       map the role to authorities
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),user.getRoles().stream().map(role->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    }
}

//new User("john_doe","hashed_password",List.of(new SimpleGrantedAuthority("ADMIN"),new SimpleGrantedAuthority("USER")));


