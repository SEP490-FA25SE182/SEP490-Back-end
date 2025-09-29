package com.sep.rookieservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtProvider {

    private final Key key = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode("ZmluZC1hLWxvbmcgMzItY2hhciBzZWNyZXQtdG8tYmUtbG9uZy0yNTYtYml0IQ==")
    );

    private static final long EXP_MILLIS = 1000L * 60 * 60 * 24 * 7; // 7 ngày

    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setId(UUID.randomUUID().toString())         // <— jti
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXP_MILLIS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Claims getClaims(String token) {
        return parse(token).getBody();
    }

    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    public String getJti(String token) {
        return getClaims(token).getId();
    }
}

