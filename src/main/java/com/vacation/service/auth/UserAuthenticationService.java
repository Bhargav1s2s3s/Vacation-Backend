package com.vacation.service.auth;

import com.vacation.dto.LoginOrSignUpRequest;
import com.vacation.dto.LoginResponse;

public interface UserAuthenticationService {
    LoginResponse createUserSignUp(LoginOrSignUpRequest request);

    LoginResponse loginUserWithCred(LoginOrSignUpRequest request);

    LoginResponse getAccessNewTokens(String refreshToken);
}
