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

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final SecretKey secretKey;
    private static final long EXPIRATION_TIME = 864_000_000;

    public JwtServiceImpl(@Value("${jwt.secret}") String secret) {
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("La clave secreta de JWT debe tener al menos 32 caracteres.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenResponse generateToken(String email, String userId, String username, String role) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

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
                .accessToken(token)
                .build();
    }

    @Override
    public TokenPayload validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return new TokenPayload(false, null, null, null);
            }
            return new TokenPayload(
                    true,
                    claims.get("userId", String.class),
                    claims.get("username", String.class),
                    claims.get("role", String.class)
            );
        } catch (Exception e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return new TokenPayload(false, null, null, null);
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