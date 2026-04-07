package com.vacation.auth.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String username;
    private String email;
}
