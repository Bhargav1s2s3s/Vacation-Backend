package com.vacation.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
    private Integer code;
    private String message;
}
