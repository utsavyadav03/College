
package com.scamshield.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTests {

    private final String secret =
            "ScamShield-Dev-Secret-Key-2026-At-Least-32-Bytes";

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        jwtService = new JwtService(secret);
    }

    @Test
    void generatedTokenShouldReturnCorrectEmail() {

        String token =
                jwtService.generateToken("usera@test.com");

        assertEquals(
                "usera@test.com",
                jwtService.extractEmail(token));
    }

    @Test
    void tokenShouldBeInvalidForDifferentEmail() {

        String token =
                jwtService.generateToken("usera@test.com");

        assertFalse(
                jwtService.isTokenValid(token, "userb@test.com"));
    }

    @Test
    void tokenSignedWithDifferentKeyShouldBeRejected() {

        SecretKey anotherKey = Keys.hmacShaKeyFor(
                "Another-Secret-Key-That-Is-32-Bytes-Long"
                        .getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .subject("usera@test.com")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 60_000))
                .signWith(anotherKey)
                .compact();

        assertThrows(
                JwtException.class,
                () -> jwtService.extractEmail(token));
    }

    @Test
    void expiredTokenShouldBeRejected() {

        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .subject("usera@test.com")
                .issuedAt(new Date(now - 120_000))
                .expiration(new Date(now - 60_000))
                .signWith(key)
                .compact();

        assertFalse(
                jwtService.isTokenValid(token, "usera@test.com"));

        assertThrows(
                JwtException.class,
                () -> jwtService.extractEmail(token));
    }
}
