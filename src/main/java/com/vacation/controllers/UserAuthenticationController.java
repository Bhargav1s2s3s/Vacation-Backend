package com.vacation.controllers;

import com.vacation.dto.LoginOrSignUpRequest;
import com.vacation.dto.LoginResponse;
import com.vacation.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserAuthenticationController extends BaseController {

    private final UserAuthenticationService userAuthenticationService;

    @PostMapping(value = "/auth/sign-up")
    public ResponseEntity<LoginResponse> requestLogin(@RequestBody LoginOrSignUpRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAuthenticationService.createUserSignUp(request));
    }
}
