package com.kugan.spring_basic.security;

import com.kugan.spring_basic.entity.Role;
import com.kugan.spring_basic.entity.User;
import com.kugan.spring_basic.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
//    secret key
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.ES512);

//    expiration time
    private final int jwtExpirationMs= 86400000;

    private UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Generate token
    public String generatetoken(String username){
        Optional<User> user = userRepository.findByUsername(username);
        Set<Role> roles = user.get().getRoles();

//        add roles into the token

        return Jwts.builder().setSubject(username)
                .claim("roles", roles.stream().map(role->role.getName()).collect(Collectors.joining(",")))
                .setIssuedAt(new Date())
                .setExpiration( new Date( new Date().getTime() + jwtExpirationMs))
                .signWith(secretKey).compact();
    }


//    extract username from token
    public String extractUsername (String token){
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

//    extract roles from token
    public Set<String> extractRoles(String token){
        String rolesString = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().get("roles", String.class);
        return Set.of(rolesString);
    }

//    token validatation
    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
