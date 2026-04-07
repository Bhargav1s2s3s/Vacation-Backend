package com.vacation.auth.exception;

import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.VacationException;

public class UsernameAlreadyExsitedEx extends VacationException {
    public UsernameAlreadyExsitedEx(String message) {
        super(VacationErrorCode.USER_ALREADY_EXISTS, message);
    }
}
