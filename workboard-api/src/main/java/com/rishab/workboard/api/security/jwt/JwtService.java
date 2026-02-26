package com.rishab.workboard.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretBase64;

    @Value("${security.jwt.expMinutes:15}")
    private long expMinutes;

    // convert base64 secret into a SecretKey used to sign/verify JWTs
    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    token includes:
    - subject: username
    - claim uid: userId
     */
    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("uid", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    // Parse and verify signature + expiry. Throws if invalid.
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Convenience boolean check.
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
