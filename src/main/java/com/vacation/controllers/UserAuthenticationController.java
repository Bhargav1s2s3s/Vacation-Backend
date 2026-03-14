package com.vacation.controllers;

import com.vacation.dto.LoginOrSignUpRequest;
import com.vacation.dto.LoginResponse;
import com.vacation.service.auth.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserAuthenticationController extends BaseController {

    private final UserAuthenticationService userAuthenticationService;

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
}
