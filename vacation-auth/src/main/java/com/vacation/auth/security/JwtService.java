package com.vacation.auth.security;

import com.vacation.auth.exception.InvalidTokenException;
import com.vacation.common.error.code.VacationErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateAccessToken(UUID userId, String email) {
        return buildToken(userId, email, expiration);
    }

    public String generateRefreshToken(UUID userId, String profileId) {
        return buildToken(userId, profileId, refreshExpiration);
    }

    private String buildToken(UUID userId, String profileId, long expiry) {
        return Jwts.builder()
                .subject(profileId)
                .claim("userId", userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public void validateAccessToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(VacationErrorCode.REFRESH_TOKEN_EXPIRED, e.getMessage());
        } catch (JwtException e) {
            throw new InvalidTokenException(VacationErrorCode.INVALID_TOKEN, e.getMessage());
        }
    }

    public void validateRefreshToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(VacationErrorCode.REFRESH_TOKEN_EXPIRED, e.getMessage());
        } catch (JwtException e) {
            throw new InvalidTokenException(VacationErrorCode.REFRESH_TOKEN_INVALID, e.getMessage());
        }
    }

    public String extractEmail(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(VacationErrorCode.EMAIL_NOT_FOUND, e.getMessage());
        } catch (JwtException e) {
            throw new InvalidTokenException(VacationErrorCode.INVALID_TOKEN, e.getMessage());
        }
    }

    public UUID extractUserId(String token) {
        try {
            String userId = parseClaims(token).get("userId", String.class);
            if (userId == null) {
                throw new InvalidTokenException(VacationErrorCode.INVALID_TOKEN, token);
            }
            return UUID.fromString(userId);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(VacationErrorCode.INVALID_TOKEN, token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(VacationErrorCode.INVALID_TOKEN, e.getMessage());
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
