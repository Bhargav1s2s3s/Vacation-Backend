package com.vacation.auth.exception;

import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.VacationException;

public class UserAuthenticationException extends VacationException {
    public UserAuthenticationException( String message) {
        super(VacationErrorCode.EMAIL_AND_PASSWORD_ARE_REQUIRED, message);
    }
}
