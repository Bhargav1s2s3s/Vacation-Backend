package com.vacation.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
