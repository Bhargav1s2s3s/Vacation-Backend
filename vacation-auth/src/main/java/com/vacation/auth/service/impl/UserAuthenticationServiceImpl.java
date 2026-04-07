package com.vacation.auth.service.impl;

import com.vacation.auth.dto.LoginOrSignUpRequest;
import com.vacation.auth.dto.LoginResponse;
import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.entity.RefreshTokenEntity;
import com.vacation.auth.entity.UserEntity;
import com.vacation.auth.exception.EmailAlreadyExistedEx;
import com.vacation.auth.exception.InvalidTokenException;
import com.vacation.auth.exception.UserAuthenticationException;
import com.vacation.auth.exception.UsernameAlreadyExsitedEx;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.auth.repository.RefreshTokenRepository;
import com.vacation.auth.repository.UserRepository;
import com.vacation.auth.service.UserAuthenticationService;
import com.vacation.auth.security.JwtService;
import com.vacation.common.error.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ProfileRepository profileRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public LoginResponse createUserSignUp(LoginOrSignUpRequest request) {

        if (request.getEmail() == null || request.getPassword() == null) {
            throw new UserAuthenticationException("Email or password required for sign up");
        }

        if (profileRepository.existsByEmailId(request.getEmail())) {
            throw new EmailAlreadyExistedEx("Email already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExsitedEx("username already registered");
        }

        // Hashing password
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);

        user = userRepository.save(user);
        log.info("user entity has been saved: {}", user);

        ProfileEntity profile = new ProfileEntity();
        profile.setUser(user);
        profile.setEmailId(request.getEmail());
        profileRepository.save(profile);
        log.info("email inserted into profile entity: {}", user);

        // Generate access token
        String access = jwtService.generateAccessToken(user.getId(), String.valueOf(profile.getId()));
        log.info("access token has been created after sign-up: {}", access);

        // Generate refresh token
        String refresh = jwtService.generateRefreshToken(user.getId(), String.valueOf(profile.getId()));
        log.info("refresh token has been created sign-up: {}", refresh);

        tokenSavedInDbWithUserId(user, refresh);

        return LoginResponse.builder()
                .code(200)
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    @Transactional
    @Override
    public LoginResponse loginUserWithCred(LoginOrSignUpRequest request) {
        if (request.getPassword() == null) {
            throw new UserAuthenticationException("Password is required for login");
        }

        UserEntity user;
        ProfileEntity profile;
        String email;

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Login with email
            profile = profileRepository.findByEmailId(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            VacationErrorCode.EMAIL_NOT_FOUND, "Email not registered"));
            user = profile.getUser();
            email = request.getEmail();

        } else if (request.getUsername() != null && !request.getUsername().isBlank()) {
            // Login with username
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            VacationErrorCode.USER_NOT_FOUND, "Username not registered"));
            profile = profileRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            VacationErrorCode.USER_NOT_FOUND, "Profile not found"));
            email = profile.getEmailId();

        } else {
            throw new UserAuthenticationException("Please provide either email or username");
        }

        // Validate password
        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserAuthenticationException("Invalid Password");
        }

        // Generate access token
        String accessToken = jwtService.generateAccessToken(user.getId(), profile.getId().toString());
        log.info("access token has been created after sign-in: {}", accessToken);

        // Generate refresh token
        String refreshToken = jwtService.generateRefreshToken(user.getId(), profile.getId().toString());
        log.info("refresh token has been created after sign-in: {}", refreshToken);

        // store refresh token
        tokenSavedInDbWithUserId(user, refreshToken);
        log.info("user refresh saved in: {}", user);

        log.info("User logged in successfully: {}", user.getUsername());
        return LoginResponse.builder()
                .code(200)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public LoginResponse getAccessNewTokens(String refreshToken) {

        // 1. Exists in DB?
        RefreshTokenEntity stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException(
                        VacationErrorCode.REFRESH_TOKEN_INVALID, "Refresh token not found"));

        // 2. Validate JWT — throws if expired or invalid
        jwtService.validateRefreshToken(refreshToken);

        // 3. Extract claims
        String email = jwtService.extractEmail(refreshToken);
        UUID userId = jwtService.extractUserId(refreshToken);

        // 4. Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(userId, email);
        String newRefreshToken = jwtService.generateRefreshToken(userId, email);

        // 5. Replace refresh token in DB
        tokenSavedInDbWithUserId(stored.getUser(), newRefreshToken);

        return LoginResponse.builder()
                .code(200)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private void tokenSavedInDbWithUserId(UserEntity user, String refreshToken) {
        int rowEffect = refreshTokenRepository.deleteByUser(user);
        log.info("refresh token has been Deleted rowEffect: {}", rowEffect);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
