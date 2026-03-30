package com.vacation.auth.service;

import com.vacation.auth.dto.ForgotPasswordRequest;
import com.vacation.auth.dto.MessageResponse;
import com.vacation.auth.dto.ResetPasswordRequest;

public interface PasswordResetService {

    MessageResponse forgotPassword(ForgotPasswordRequest request);

    MessageResponse validateResetToken(String token);

    MessageResponse resetPassword(String token, ResetPasswordRequest request);
}
