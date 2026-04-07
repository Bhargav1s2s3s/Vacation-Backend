package com.vacation.auth.dto;

import lombok.Data;

@Data
public class LoginOrSignUpRequest {
    private String username;
    private String password;
    private String email;
}
