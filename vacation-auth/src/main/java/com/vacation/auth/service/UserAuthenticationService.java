package com.vacation.auth.service;

import com.vacation.auth.dto.LoginOrSignUpRequest;
import com.vacation.auth.dto.LoginResponse;

public interface UserAuthenticationService {
    LoginResponse createUserSignUp(LoginOrSignUpRequest request);

    LoginResponse loginUserWithCred(LoginOrSignUpRequest request);

    LoginResponse getAccessNewTokens(String refreshToken);
}
