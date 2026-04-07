package com.vacation.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
