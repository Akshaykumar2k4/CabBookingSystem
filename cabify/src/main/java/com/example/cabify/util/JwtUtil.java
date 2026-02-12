package com.example.cabify.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 🚀 FIXED KEY: Ensures signatures match after server restarts
    private final String SECRET_STRING = "YourElectricBlueDriverPortalSecretKeyMustBeVeryLong123!";
    private final SecretKey signInKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    public String generateToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String userName) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userName) && !isTokenExpired(token));
    }

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