package com.guard.vaultguard.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

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

    public boolean validateToken(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaims(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isExpired(String token){
        return extractExpiration(token).before(new Date());
    }
}

