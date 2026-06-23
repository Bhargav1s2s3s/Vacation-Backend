package com.vacation.auth.security;

import com.vacation.auth.exception.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// ============================================================
//  JwtServiceTest — Tests a class that has NO dependencies
//  (no @Mock needed), so we do NOT use MockitoExtension here.
//
//  JwtService only uses @Value fields from Spring. To test it
//  WITHOUT starting Spring, we use ReflectionTestUtils to
//  inject values into private fields manually.
// ============================================================
class JwtServiceTest {

    private JwtService jwtService;

    // A valid 256-bit base64-encoded secret key for testing
    // (Never use this in production — it's just for tests!)
    private static final String TEST_SECRET =
            Base64.getEncoder().encodeToString(
                    "vacation-app-super-secret-key-for-testing-32b".getBytes()
            );

    private static final long ACCESS_EXPIRY  = 1000 * 60 * 15;   // 15 minutes
    private static final long REFRESH_EXPIRY = 1000 * 60 * 60 * 24; // 24 hours

    private UUID testUserId;
    private String testProfileId;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // ============================================================
        //  ReflectionTestUtils.setField(...)
        //  This lets you SET private fields that normally can't be touched.
        //  In production, Spring injects @Value fields. In tests, we do it
        //  manually with this utility method.
        //
        //  Arguments: (object, "fieldName", value)
        // ============================================================
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", ACCESS_EXPIRY);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", REFRESH_EXPIRY);

        testUserId    = UUID.randomUUID();
        testProfileId = UUID.randomUUID().toString();
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class GenerationTests {

        @Test
        @DisplayName("✅ generateAccessToken() should return a non-blank JWT string")
        void generateAccessToken_ShouldReturnToken() {
            String token = jwtService.generateAccessToken(testUserId, testProfileId);

            // A JWT always has the format: header.payload.signature (3 parts)
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("✅ generateRefreshToken() should return a valid JWT")
        void generateRefreshToken_ShouldReturnToken() {
            String token = jwtService.generateRefreshToken(testUserId, testProfileId);

            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("✅ Access token and refresh token should be different strings")
        void accessAndRefreshTokens_ShouldBeDifferent() {
            String access  = jwtService.generateAccessToken(testUserId, testProfileId);
            String refresh = jwtService.generateRefreshToken(testUserId, testProfileId);

            // Even with same userId, they differ because expiry is different,
            // meaning the iat/exp claims differ (plus "issuedAt" milliseconds may differ)
            assertThat(access).isNotEqualTo(refresh);
        }
    }

    @Nested
    @DisplayName("Claim Extraction Tests")
    class ExtractionTests {

        @Test
        @DisplayName("✅ extractEmail() should return the profileId set as subject")
        void extractEmail_ShouldReturnProfileId() {
            String token = jwtService.generateAccessToken(testUserId, testProfileId);

            String extracted = jwtService.extractEmail(token);

            // In your JwtService, profileId is set as the "subject" of the token
            assertThat(extracted).isEqualTo(testProfileId);
        }

        @Test
        @DisplayName("✅ extractUserId() should return the correct UUID")
        void extractUserId_ShouldReturnCorrectUUID() {
            String token = jwtService.generateAccessToken(testUserId, testProfileId);

            UUID extracted = jwtService.extractUserId(token);

            assertThat(extracted).isEqualTo(testUserId);
        }

        @Test
        @DisplayName("❌ extractUserId() should throw for a completely invalid token string")
        void extractUserId_ShouldThrow_ForGarbageToken() {
            assertThatThrownBy(() -> jwtService.extractUserId("this.is.garbage"))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("✅ validateAccessToken() should not throw for a valid token")
        void validateAccessToken_ShouldNotThrow_ForValidToken() {
            String token = jwtService.generateAccessToken(testUserId, testProfileId);

            // If this doesn't throw, the test passes
            // org.junit.jupiter.api.Assertions.assertDoesNotThrow would also work
            jwtService.validateAccessToken(token);
        }

        @Test
        @DisplayName("❌ validateAccessToken() should throw for an expired token")
        void validateAccessToken_ShouldThrow_ForExpiredToken() {
            // ============================================================
            //  HOW TO TEST EXPIRY WITHOUT ACTUALLY WAITING?
            //  Build a token manually with expiry set to the PAST.
            //  We use the same secret key so the service can verify it.
            // ============================================================
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));

            String expiredToken = Jwts.builder()
                    .subject(testProfileId)
                    .claim("userId", testUserId.toString())
                    .issuedAt(new Date(System.currentTimeMillis() - 10_000)) // issued 10s ago
                    .expiration(new Date(System.currentTimeMillis() - 5_000)) // expired 5s ago
                    .signWith(key)
                    .compact();

            assertThatThrownBy(() -> jwtService.validateAccessToken(expiredToken))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("❌ validateRefreshToken() should throw for a tampered token")
        void validateRefreshToken_ShouldThrow_ForTamperedToken() {
            String validToken = jwtService.generateRefreshToken(testUserId, testProfileId);

            // Tamper with the token by corrupting the signature part
            String[] parts = validToken.split("\\.");
            String tamperedToken = parts[0] + "." + parts[1] + ".TAMPERED_SIGNATURE";

            assertThatThrownBy(() -> jwtService.validateRefreshToken(tamperedToken))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }
}
