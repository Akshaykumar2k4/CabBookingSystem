package com.example.cabify.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    //This variable will hold the random bytes in RAM
    private Key signInKey;

    // Token is valid for 10 hours
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    /**
     * Generates a JWT for the authenticated user (e.g., after successful login).
     */
    // This method generates the key when the app starts
    @PostConstruct
    protected void init() {
        this.signInKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    public String generateToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the token from the request header.
     * Checks if the signature is valid and if the token belongs to the user.
     */
    public boolean validateToken(String token, String userName) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userName) && !isTokenExpired(token));
    }

    // --- Helper Methods ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}