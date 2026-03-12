package com.vacation.dto;

import lombok.*;

@Data
@Builder
public class LoginResponse {
    private Integer code;
    private String accessToken;
    private String refreshToken;
}
