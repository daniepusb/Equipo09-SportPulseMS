package com.sportpulse.ms_auth.service.impl;

import com.sportpulse.ms_auth.common.model.dto.response.TokenPayload;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import com.sportpulse.ms_auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration) {
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("La clave secreta de JWT debe tener al menos 32 caracteres.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = accessExpiration;
    }

    @Override
    public TokenResponse generateToken(String email, String userId, String username, String role) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;

        String token = Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", normalizedRole)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();

        return TokenResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(TimeUnit.MILLISECONDS.toSeconds(expirationTime))
                .userId(userId)
                .build();
    }

    @Override
    public TokenPayload validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return new TokenPayload(false, null, null, null, "TOKEN_EXPIRED");
            }
            return new TokenPayload(
                    true,
                    claims.get("userId", String.class),
                    claims.get("username", String.class),
                    claims.get("role", String.class),
                    null
            );
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("expirado")) {
                return new TokenPayload(false, null, null, null, "TOKEN_EXPIRED");
            }
            log.warn("Token JWT inválido: {}", e.getMessage());
            return new TokenPayload(false, null, null, null, "INVALID_TOKEN");
        } catch (Exception e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return new TokenPayload(false, null, null, null, "INVALID_TOKEN");
        }
    }

    @Override
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error al parsear JWT: {}, Causa: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
            throw new IllegalArgumentException("Token JWT inválido o expirado", e);
        }
    }

    @Override
    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    @Override
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }
}