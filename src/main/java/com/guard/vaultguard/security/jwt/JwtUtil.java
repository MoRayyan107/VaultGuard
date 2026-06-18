package com.guard.vaultguard.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private final Key secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    public JwtUtil (@Value("${app.jwt.secret}") String key) {
        byte[] convertedByteKey = Base64.getDecoder().decode(key);
        this.secretKey = Keys.hmacShaKeyFor(convertedByteKey);
    }

    public String generateToken(String username, String role) // will assign every user a role
    {
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("role", role);

        return Jwts.builder()
                .claims()
                .add(userClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .and()
                .signWith(secretKey)
                .compact();
    }

    // TODO
    public boolean validateToken(String token)
    {
        return true;
    }

    public String extractUsername(String token)
    {
        return null;
    }

    public String extractRole(String token)
    {
        return null;
    }

    public Date extractExpiration(String token)
    {
        return null;
    }

    public Claims extractAllClaims(String token)
    {
        return null;
    }
}

