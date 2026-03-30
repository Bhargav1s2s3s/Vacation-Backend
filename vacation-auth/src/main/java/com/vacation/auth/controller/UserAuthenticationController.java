package com.vacation.auth.controller;

import com.vacation.auth.dto.*;
import com.vacation.auth.service.PasswordResetService;
import com.vacation.auth.service.UserAuthenticationService;
import com.vacation.common.config.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserAuthenticationController extends BaseController {

    private final UserAuthenticationService userAuthenticationService;
    private final PasswordResetService passwordResetService;

    @PostMapping(value = "/auth/sign-up")
    public ResponseEntity<LoginResponse> signUp(@RequestBody LoginOrSignUpRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAuthenticationService.createUserSignUp(request));
    }

    @PostMapping(value = "/auth/sign-in")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginOrSignUpRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAuthenticationService.loginUserWithCred(request));
    }

    @GetMapping(value = "/auth/get-new-tokens")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAuthenticationService.getAccessNewTokens(refreshToken));
    }

    @PostMapping(value = "/auth/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(passwordResetService.forgotPassword(request));
    }

    @GetMapping(value = "/auth/reset-password/{token:.+}")
    public ResponseEntity<MessageResponse> validateResetToken(@PathVariable String token) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(passwordResetService.validateResetToken(token));
    }

    @PostMapping(value = "/auth/reset-password/{token:.+}")
    public ResponseEntity<MessageResponse> resetPassword(
            @PathVariable String token,
            @RequestBody ResetPasswordRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(passwordResetService.resetPassword(token, request));
    }
}
