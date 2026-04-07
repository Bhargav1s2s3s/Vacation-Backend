package com.vacation.auth.service.impl;

import com.vacation.auth.dto.ForgotPasswordRequest;
import com.vacation.auth.dto.MessageResponse;
import com.vacation.auth.dto.ResetPasswordRequest;
import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.entity.UserEntity;
import com.vacation.auth.entity.UserPasswordResetEntity;
import com.vacation.auth.exception.PasswordResetException;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.auth.repository.UserPasswordResetRepository;
import com.vacation.auth.repository.UserRepository;
import com.vacation.auth.security.PasswordResetJwtService;
import com.vacation.auth.service.EmailService;
import com.vacation.auth.service.PasswordResetService;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final ProfileRepository profileRepository;
    private final UserPasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordResetJwtService passwordResetJwtService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {

        ProfileEntity profile;
        UserEntity user;
        String email;

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Find by email
            profile = profileRepository.findByEmailId(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            VacationErrorCode.EMAIL_NOT_FOUND, "Email not registered"));
            user = profile.getUser();
            email = request.getEmail();

        } else if (request.getUsername() != null && !request.getUsername().isBlank()) {
            // Find by username
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            VacationErrorCode.USER_NOT_FOUND, "Username not registered"));
            profile = profileRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            VacationErrorCode.EMAIL_NOT_FOUND, "Profile not found for this user"));
            email = profile.getEmailId();

        } else {
            throw new PasswordResetException("Please provide either email or username");
        }

        // 2. Delete any existing reset tokens for this user
        int deleted = passwordResetRepository.deleteByUser(user);
        log.info("Deleted {} old reset tokens for user: {}", deleted, user.getUsername());

        // 3. Generate JWT reset token
        String resetToken = passwordResetJwtService.generatePasswordResetToken(email);
        log.info("Password reset token generated for: {}", email);

        // 4. Save token in DB
        UserPasswordResetEntity resetEntity = new UserPasswordResetEntity();
        resetEntity.setUser(user);
        resetEntity.setResetToken(resetToken);
        passwordResetRepository.save(resetEntity);

        // 5. Send email with reset link
        emailService.sendPasswordResetEmail(email, resetToken);

        return MessageResponse.builder()
                .code(200)
                .message("Password reset link sent to your email")
                .build();
    }

    @Override
    public MessageResponse validateResetToken(String token) {
        // 1. Validate JWT token (checks signature, expiry, type=PASSWORD_RESET)
        String email = passwordResetJwtService.validateAndExtractEmail(token);
        log.info("Password reset token validated for email: {}", email);

        // 2. Check token exists in DB (not already used)
        passwordResetRepository.findByResetToken(token)
                .orElseThrow(() -> new PasswordResetException("Reset token not found or already used"));

        return MessageResponse.builder()
                .code(200)
                .message("Token is valid. You can reset your password.")
                .build();
    }

    @Transactional
    @Override
    public MessageResponse resetPassword(String token, ResetPasswordRequest request) {
        // 1. Validate JWT token (checks signature, expiry, type=PASSWORD_RESET)
        String email = passwordResetJwtService.validateAndExtractEmail(token);
        log.info("Password reset token validated for email: {}", email);

        // 2. Check token exists in DB
        UserPasswordResetEntity resetEntity = passwordResetRepository
                .findByResetToken(token)
                .orElseThrow(() -> new PasswordResetException("Reset token not found or already used"));

        // 3. Get user
        UserEntity user = resetEntity.getUser();

        // 4. Encode and update password
        String encodedPassword = bCryptPasswordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        log.info("Password updated for user: {}", user.getUsername());

        // 5. Delete the token entry (stateless — remove after use)
        passwordResetRepository.deleteByUser(user);
        log.info("Reset token deleted for user: {}", user.getUsername());

        return MessageResponse.builder()
                .code(200)
                .message("Password reset successful")
                .build();
    }
}
