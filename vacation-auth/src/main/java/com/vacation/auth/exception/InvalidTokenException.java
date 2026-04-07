package com.vacation.auth.exception;

import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.VacationException;

public class InvalidTokenException extends VacationException {
    public InvalidTokenException(VacationErrorCode error, String message) {
        super(error, message);
    }
}
