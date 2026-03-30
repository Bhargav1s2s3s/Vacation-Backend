package com.vacation.auth.security;

import com.vacation.auth.exception.PasswordResetException;
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

@Service
public class PasswordResetJwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${password-reset.token-expiry-minutes}")
    private long expiryMinutes;

    private static final String CLAIM_TYPE = "type";
    private static final String PASSWORD_RESET_TYPE = "PASSWORD_RESET";

    public String generatePasswordResetToken(String email) {
        long expiryMs = expiryMinutes * 60 * 1000;

        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TYPE, PASSWORD_RESET_TYPE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String validateAndExtractEmail(String token) {
        try {
            Claims claims = parseClaims(token);

            String type = claims.get(CLAIM_TYPE, String.class);
            if (!PASSWORD_RESET_TYPE.equals(type)) {
                throw new PasswordResetException("Invalid token type");
            }

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new PasswordResetException("Password reset token has expired");
        } catch (JwtException e) {
            throw new PasswordResetException("Invalid password reset token");
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
