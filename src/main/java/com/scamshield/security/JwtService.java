
package com.scamshield.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {

        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + 60 * 60 * 1000L))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, String email) {

        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
