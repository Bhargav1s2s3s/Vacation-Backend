package com.vacation.auth.dto;

import lombok.*;

@Data
@Builder
public class LoginResponse {
    private Integer code;
    private String accessToken;
    private String refreshToken;
}
