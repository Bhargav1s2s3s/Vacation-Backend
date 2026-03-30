package com.vacation.common.error.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class VacationErrorExceptionDto {
    private Enum code;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}
