package com.vacation.error.exception;

import com.vacation.error.code.VacationErrorCode;

public class InvalidTokenException extends VacationException {
    public InvalidTokenException(VacationErrorCode error, String message) {
        super(error, message);
    }
}
