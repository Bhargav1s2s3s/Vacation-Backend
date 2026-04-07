package com.vacation.auth.exception;

import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.VacationException;

public class EmailAlreadyExistedEx extends VacationException {
    public EmailAlreadyExistedEx(String message) {
        super(VacationErrorCode.EMAIL_ALREADY_EXISTS, message);
    }
}
