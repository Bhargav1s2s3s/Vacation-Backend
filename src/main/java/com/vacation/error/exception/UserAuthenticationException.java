package com.vacation.error.exception;

import com.vacation.error.code.VacationErrorCode;

public class UserAuthenticationException extends VacationException {
    public UserAuthenticationException( String message) {
        super(VacationErrorCode.EMAIL_AND_PASSWORD_ARE_REQUIRED, message);
    }
}
