package com.vacation.auth.service;

import com.vacation.auth.dto.LoginOrSignUpRequest;
import com.vacation.auth.dto.LoginResponse;
import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.entity.RefreshTokenEntity;
import com.vacation.auth.entity.UserEntity;
import com.vacation.auth.exception.EmailAlreadyExistedEx;
import com.vacation.auth.exception.InvalidTokenException;
import com.vacation.auth.exception.UserAuthenticationException;
import com.vacation.auth.exception.UsernameAlreadyExsitedEx;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.auth.repository.RefreshTokenRepository;
import com.vacation.auth.repository.UserRepository;
import com.vacation.auth.security.JwtService;
import com.vacation.auth.service.impl.UserAuthenticationServiceImpl;
import com.vacation.common.error.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ============================================================
//  WHAT IS @ExtendWith(MockitoExtension.class)?
//  This tells JUnit 5 to activate Mockito's magic.
//  Without this, @Mock and @InjectMocks annotations do NOTHING.
//  Think of it as plugging in Mockito into the JUnit framework.
// ============================================================
@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceImplTest {

    // ============================================================
    //  WHAT IS @Mock?
    //  A Mock is a FAKE object. It looks like the real class
    //  but does NOTHING by default (returns null/0/false).
    //  You control what it returns using when(...).thenReturn(...)
    //
    //  WHY? Because UserAuthenticationServiceImpl depends on
    //  UserRepository, JwtService etc. We do NOT want to:
    //   - Connect to a real database
    //   - Send real JWTs
    //  We just want to test the SERVICE LOGIC ONLY.
    // ============================================================
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    // ============================================================
    //  WHAT IS @InjectMocks?
    //  This creates a REAL instance of UserAuthenticationServiceImpl
    //  and automatically injects all @Mock objects above into it.
    //  So the service uses FAKE repos and FAKE jwt — perfect for testing.
    // ============================================================
    @InjectMocks
    private UserAuthenticationServiceImpl userAuthService;

    // ============================================================
    //  @BeforeEach runs BEFORE every single test method.
    //  Use it to set up common data so you don't repeat yourself.
    // ============================================================
    private LoginOrSignUpRequest validRequest;
    private UserEntity savedUser;
    private ProfileEntity savedProfile;

    @BeforeEach
    void setUp() {
        // Build a common request object used across multiple tests
        validRequest = new LoginOrSignUpRequest();
        validRequest.setEmail("bhargav@example.com");
        validRequest.setPassword("secret123");
        validRequest.setUsername("bhargav");

        // Build a fake UserEntity that the mock repository will "return"
        savedUser = new UserEntity();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("bhargav");
        savedUser.setPassword("$2a$encoded_password");  // Simulates BCrypt hash

        // Build a fake ProfileEntity
        savedProfile = new ProfileEntity();
        savedProfile.setId(UUID.randomUUID());
        savedProfile.setUser(savedUser);
        savedProfile.setEmailId("bhargav@example.com");
    }

    // ============================================================
    //  @Nested lets you GROUP related tests together.
    //  This makes the test report much easier to read.
    //  All tests inside are still normal JUnit tests.
    // ============================================================
    @Nested
    @DisplayName("Sign Up Tests - createUserSignUp()")
    class SignUpTests {

        // ============================================================
        //  TEST 1: The Happy Path — everything goes right
        //
        //  PATTERN: Arrange → Act → Assert (AAA)
        //   Arrange: Set up all the mocks to return what we want
        //   Act:     Call the actual method under test
        //   Assert:  Check that the result is what we expect
        // ============================================================
        @Test
        @DisplayName("✅ Should return LoginResponse with tokens when sign-up is successful")
        void signUp_ShouldReturnTokens_WhenRequestIsValid() {

            // ---- ARRANGE ----
            // Tell mock: "if anyone asks if email exists, say NO (not taken)"
            when(profileRepository.existsByEmailId("bhargav@example.com")).thenReturn(false);

            // Tell mock: "if anyone asks if username exists, say NO"
            when(userRepository.existsByUsername("bhargav")).thenReturn(false);

            // Tell the encoder mock: encode any password → return fake hash
            when(bCryptPasswordEncoder.encode("secret123")).thenReturn("$2a$hashed");

            // Tell the user repo: when we save any UserEntity, return our savedUser
            when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

            // Tell the profile repo: when we save any ProfileEntity, return our savedProfile
            when(profileRepository.save(any(ProfileEntity.class))).thenReturn(savedProfile);

            // Tell jwt service: generate an access token → return this fake string
            when(jwtService.generateAccessToken(any(UUID.class), anyString())).thenReturn("fake-access-token");

            // Tell jwt service: generate a refresh token → return this fake string
            when(jwtService.generateRefreshToken(any(UUID.class), anyString())).thenReturn("fake-refresh-token");

            // Tell refresh token repo: deleting old token for user → affects 1 row
            when(refreshTokenRepository.deleteByUser(any(UserEntity.class))).thenReturn(1);

            // Tell refresh token repo: saving new token → return any entity
            when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenReturn(new RefreshTokenEntity());

            // ---- ACT ----
            // Now call the REAL method. It will use all the mocks above.
            LoginResponse response = userAuthService.createUserSignUp(validRequest);

            // ---- ASSERT ----
            // assertThat is from AssertJ — a fluent, readable assertion library
            assertThat(response).isNotNull();
            assertThat(response.getCode()).isEqualTo(200);
            assertThat(response.getAccessToken()).isEqualTo("fake-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("fake-refresh-token");

            // VERIFY: Did the method actually call userRepository.save() exactly once?
            // This catches bugs like "the method forgot to save the user"
            verify(userRepository, times(1)).save(any(UserEntity.class));
            verify(profileRepository, times(1)).save(any(ProfileEntity.class));
            verify(refreshTokenRepository, times(1)).save(any(RefreshTokenEntity.class));
        }

        // ============================================================
        //  TEST 2: Null email — should throw exception
        //
        //  We test that when INVALID INPUT is given, the service
        //  throws the RIGHT exception with a meaningful message.
        // ============================================================
        @Test
        @DisplayName("❌ Should throw UserAuthenticationException when email is null")
        void signUp_ShouldThrowException_WhenEmailIsNull() {

            // ---- ARRANGE ----
            validRequest.setEmail(null);  // remove the email to simulate bad input

            // ---- ACT + ASSERT (combined using assertThatThrownBy) ----
            // This is AssertJ's way to assert that calling the method THROWS an exception.
            assertThatThrownBy(() -> userAuthService.createUserSignUp(validRequest))
                    .isInstanceOf(UserAuthenticationException.class)
                    .hasMessageContaining("Email or password required");

            // Since it threw before even reaching the repo, verify it was NEVER called
            verifyNoInteractions(profileRepository);
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("❌ Should throw UserAuthenticationException when password is null")
        void signUp_ShouldThrowException_WhenPasswordIsNull() {
            validRequest.setPassword(null);

            assertThatThrownBy(() -> userAuthService.createUserSignUp(validRequest))
                    .isInstanceOf(UserAuthenticationException.class);
        }

        // ============================================================
        //  TEST 3: Email already exists — duplicate check
        //
        //  Here we tell the mock "yes, email is taken" and confirm
        //  that the service correctly rejects the signup.
        // ============================================================
        @Test
        @DisplayName("❌ Should throw EmailAlreadyExistedEx when email is already registered")
        void signUp_ShouldThrowEmailException_WhenEmailExists() {

            // ---- ARRANGE ----
            // Simulate: email already exists in the database
            when(profileRepository.existsByEmailId("bhargav@example.com")).thenReturn(true);

            // ---- ACT + ASSERT ----
            assertThatThrownBy(() -> userAuthService.createUserSignUp(validRequest))
                    .isInstanceOf(EmailAlreadyExistedEx.class)
                    .hasMessageContaining("Email already registered");

            // The user repo should never have been reached
            verifyNoInteractions(userRepository);
        }

        // ============================================================
        //  TEST 4: Username already exists
        // ============================================================
        @Test
        @DisplayName("❌ Should throw UsernameAlreadyExsitedEx when username is taken")
        void signUp_ShouldThrowUsernameException_WhenUsernameExists() {

            // Email is free, but username is taken
            when(profileRepository.existsByEmailId("bhargav@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("bhargav")).thenReturn(true);

            assertThatThrownBy(() -> userAuthService.createUserSignUp(validRequest))
                    .isInstanceOf(UsernameAlreadyExsitedEx.class)
                    .hasMessageContaining("username already registered");
        }
    }

    // ============================================================
    //  GROUP 2: Login Tests
    // ============================================================
    @Nested
    @DisplayName("Login Tests - loginUserWithCred()")
    class LoginTests {

        @Test
        @DisplayName("✅ Should login successfully using email and password")
        void login_ShouldReturnTokens_WhenEmailAndPasswordAreCorrect() {

            // ---- ARRANGE ----
            // Only email provided (no username in this test)
            validRequest.setUsername(null);

            // Mock: profileRepository finds the profile by email
            when(profileRepository.findByEmailId("bhargav@example.com"))
                    .thenReturn(Optional.of(savedProfile));

            // Mock: password matches (BCrypt check returns true)
            when(bCryptPasswordEncoder.matches("secret123", "$2a$encoded_password"))
                    .thenReturn(true);

            // Mock: JWT generation
            when(jwtService.generateAccessToken(any(UUID.class), anyString()))
                    .thenReturn("new-access-token");
            when(jwtService.generateRefreshToken(any(UUID.class), anyString()))
                    .thenReturn("new-refresh-token");

            // Mock: refresh token deletion + save
            when(refreshTokenRepository.deleteByUser(any())).thenReturn(1);
            when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity());

            // ---- ACT ----
            LoginResponse response = userAuthService.loginUserWithCred(validRequest);

            // ---- ASSERT ----
            assertThat(response.getCode()).isEqualTo(200);
            assertThat(response.getAccessToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        }

        @Test
        @DisplayName("✅ Should login successfully using username and password")
        void login_ShouldReturnTokens_WhenUsernameAndPasswordAreCorrect() {

            // Only username provided (no email)
            validRequest.setEmail(null);

            when(userRepository.findByUsername("bhargav"))
                    .thenReturn(Optional.of(savedUser));
            when(profileRepository.findByUser(savedUser))
                    .thenReturn(Optional.of(savedProfile));
            when(bCryptPasswordEncoder.matches("secret123", "$2a$encoded_password"))
                    .thenReturn(true);
            when(jwtService.generateAccessToken(any(UUID.class), anyString()))
                    .thenReturn("access-token-123");
            when(jwtService.generateRefreshToken(any(UUID.class), anyString()))
                    .thenReturn("refresh-token-123");
            when(refreshTokenRepository.deleteByUser(any())).thenReturn(1);
            when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity());

            LoginResponse response = userAuthService.loginUserWithCred(validRequest);

            assertThat(response.getAccessToken()).isEqualTo("access-token-123");
        }

        // ============================================================
        //  TEST: Wrong password — should throw exception
        //
        //  BCryptPasswordEncoder.matches() returns false → wrong password
        // ============================================================
        @Test
        @DisplayName("❌ Should throw UserAuthenticationException when password is wrong")
        void login_ShouldThrowException_WhenPasswordIsWrong() {

            validRequest.setUsername(null);

            when(profileRepository.findByEmailId("bhargav@example.com"))
                    .thenReturn(Optional.of(savedProfile));

            // Simulate wrong password → BCrypt check returns FALSE
            when(bCryptPasswordEncoder.matches("secret123", "$2a$encoded_password"))
                    .thenReturn(false);

            assertThatThrownBy(() -> userAuthService.loginUserWithCred(validRequest))
                    .isInstanceOf(UserAuthenticationException.class)
                    .hasMessageContaining("Invalid Password");
        }

        @Test
        @DisplayName("❌ Should throw exception when password field is null")
        void login_ShouldThrowException_WhenPasswordIsNull() {
            validRequest.setPassword(null);

            assertThatThrownBy(() -> userAuthService.loginUserWithCred(validRequest))
                    .isInstanceOf(UserAuthenticationException.class)
                    .hasMessageContaining("Password is required");
        }

        @Test
        @DisplayName("❌ Should throw exception when neither email nor username is provided")
        void login_ShouldThrowException_WhenNoIdentifierProvided() {
            validRequest.setEmail(null);
            validRequest.setUsername(null);

            assertThatThrownBy(() -> userAuthService.loginUserWithCred(validRequest))
                    .isInstanceOf(UserAuthenticationException.class)
                    .hasMessageContaining("Please provide either email or username");
        }

        @Test
        @DisplayName("❌ Should throw ResourceNotFoundException when email is not registered")
        void login_ShouldThrow_WhenEmailNotFound() {
            validRequest.setUsername(null);

            // Simulate: no profile found for this email
            when(profileRepository.findByEmailId("bhargav@example.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userAuthService.loginUserWithCred(validRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ============================================================
    //  GROUP 3: Refresh Token Tests
    // ============================================================
    @Nested
    @DisplayName("Token Refresh Tests - getAccessNewTokens()")
    class RefreshTokenTests {

        @Test
        @DisplayName("✅ Should return new tokens when refresh token is valid")
        void refreshToken_ShouldReturnNewTokens_WhenTokenIsValid() {
            String oldRefreshToken = "valid-refresh-token";

            RefreshTokenEntity storedToken = new RefreshTokenEntity();
            storedToken.setUser(savedUser);
            storedToken.setToken(oldRefreshToken);

            // Simulate: token exists in DB
            when(refreshTokenRepository.findByToken(oldRefreshToken))
                    .thenReturn(Optional.of(storedToken));

            // Simulate: JWT validation passes (no exception thrown)
            doNothing().when(jwtService).validateRefreshToken(oldRefreshToken);

            // Simulate: extract claims from the refresh token
            when(jwtService.extractEmail(oldRefreshToken)).thenReturn("profile-id-string");
            when(jwtService.extractUserId(oldRefreshToken)).thenReturn(savedUser.getId());

            // Simulate: generate brand new tokens
            when(jwtService.generateAccessToken(any(UUID.class), anyString()))
                    .thenReturn("brand-new-access-token");
            when(jwtService.generateRefreshToken(any(UUID.class), anyString()))
                    .thenReturn("brand-new-refresh-token");

            when(refreshTokenRepository.deleteByUser(any())).thenReturn(1);
            when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity());

            LoginResponse response = userAuthService.getAccessNewTokens(oldRefreshToken);

            assertThat(response.getCode()).isEqualTo(200);
            assertThat(response.getAccessToken()).isEqualTo("brand-new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("brand-new-refresh-token");
        }

        @Test
        @DisplayName("❌ Should throw InvalidTokenException when token does not exist in DB")
        void refreshToken_ShouldThrow_WhenTokenNotFoundInDb() {
            String fakeToken = "token-that-doesnt-exist-in-db";

            // Simulate: no token found in database
            when(refreshTokenRepository.findByToken(fakeToken))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userAuthService.getAccessNewTokens(fakeToken))
                    .isInstanceOf(InvalidTokenException.class);

            // JWT should never be called if the token isn't even in the DB
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("❌ Should throw InvalidTokenException when JWT is expired")
        void refreshToken_ShouldThrow_WhenJwtIsExpired() {
            String expiredToken = "expired-jwt-token";

            RefreshTokenEntity storedToken = new RefreshTokenEntity();
            storedToken.setUser(savedUser);
            storedToken.setToken(expiredToken);

            // Token exists in DB
            when(refreshTokenRepository.findByToken(expiredToken))
                    .thenReturn(Optional.of(storedToken));

            // But JWT validation THROWS (token is expired)
            doThrow(new InvalidTokenException(null, "Token expired"))
                    .when(jwtService).validateRefreshToken(expiredToken);

            assertThatThrownBy(() -> userAuthService.getAccessNewTokens(expiredToken))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }
}
