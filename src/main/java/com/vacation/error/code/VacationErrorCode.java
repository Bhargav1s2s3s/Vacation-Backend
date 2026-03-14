package com.vacation.error.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum VacationErrorCode {

    // 400
    EMAIL_AND_PASSWORD_ARE_REQUIRED(HttpStatus.BAD_REQUEST),

    // 401
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND),

    // 409
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT);

    private final HttpStatus status;

    VacationErrorCode(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

}
