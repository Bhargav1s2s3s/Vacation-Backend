package com.vacation.auth.exception;

import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.VacationException;

public class PasswordResetException extends VacationException {
    public PasswordResetException(String message) {
        super(VacationErrorCode.PASSWORD_RESET_TOKEN_INVALID, message);
    }
}
