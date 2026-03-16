package com.vacation.auth.dto;

import lombok.Data;

@Data
public class TokenRequest {
    private String refreshToken;
}
