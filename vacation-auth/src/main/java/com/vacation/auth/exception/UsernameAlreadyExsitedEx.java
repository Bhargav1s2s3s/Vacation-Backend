package com.vacation.common.error.exception;

import com.vacation.common.error.code.VacationErrorCode;

public class UsernameAlreadyExsitedEx extends VacationException {
    public UsernameAlreadyExsitedEx(String message) {
        super(VacationErrorCode.USER_ALREADY_EXISTS, message);
    }
}
