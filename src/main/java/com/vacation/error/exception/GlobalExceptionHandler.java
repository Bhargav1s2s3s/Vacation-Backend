package com.vacation.error.exception;


import com.vacation.error.code.VacationErrorCode;
import com.vacation.error.dto.VacationErrorExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VacationException.class)
    public ResponseEntity<VacationErrorExceptionDto> vacationExceptionHandler(
            VacationException ex, WebRequest req
    ) {
        VacationErrorCode errorCode = ex.getError();

        return ResponseEntity.status(errorCode.getStatus()).body(
                VacationErrorExceptionDto.builder()
                        .code(errorCode)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.from(Instant.now()))
                        .path(req.getDescription(true).replace("uri=", ""))
                        .build()
        );
    }
}
