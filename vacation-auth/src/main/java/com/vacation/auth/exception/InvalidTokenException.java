package com.vacation.common.error.exception;

import com.vacation.common.error.code.VacationErrorCode;

public class InvalidTokenException extends VacationException {
    public InvalidTokenException(VacationErrorCode error, String message) {
        super(error, message);
    }
}
