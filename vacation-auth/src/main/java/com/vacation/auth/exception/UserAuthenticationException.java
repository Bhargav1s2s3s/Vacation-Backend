package com.vacation.common.error.exception;

import com.vacation.common.error.code.VacationErrorCode;

public class UserAuthenticationException extends VacationException {
    public UserAuthenticationException( String message) {
        super(VacationErrorCode.EMAIL_AND_PASSWORD_ARE_REQUIRED, message);
    }
}
