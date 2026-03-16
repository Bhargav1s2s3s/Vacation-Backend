package com.vacation.common.error.exception;


import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.dto.VacationErrorExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
                        .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                        .path(req.getDescription(false).replace("uri=", ""))
                        .build()
        );
    }
}
