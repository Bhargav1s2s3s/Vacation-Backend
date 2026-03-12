package com.vacation.service;

import com.vacation.dto.LoginOrSignUpRequest;
import com.vacation.dto.LoginResponse;

public interface UserAuthenticationService {
    LoginResponse createUserSignUp(LoginOrSignUpRequest request);
}
