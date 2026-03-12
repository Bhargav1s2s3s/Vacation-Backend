package com.vacation.error.exception;

import com.vacation.error.code.VacationErrorCode;

public class EmailAlreadyExistedEx extends VacationException {
    public EmailAlreadyExistedEx(String message) {
        super(VacationErrorCode.EMAIL_ALREADY_EXISTS, message);
    }
}
