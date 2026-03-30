package com.vacation.common.error.exception;


import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.dto.VacationErrorExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<VacationErrorExceptionDto> handleGenericException(
            Exception ex, WebRequest req
    ) {
        log.error("Unhandled exception caught: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                VacationErrorExceptionDto.builder()
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                        .path(req.getDescription(false).replace("uri=", ""))
                        .build()
        );
    }
}
