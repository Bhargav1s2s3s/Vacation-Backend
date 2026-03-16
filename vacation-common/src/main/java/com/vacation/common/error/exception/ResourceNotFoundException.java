package com.vacation.common.error.exception;

import com.vacation.common.error.code.VacationErrorCode;

public class ResourceNotFoundException extends VacationException {
    public ResourceNotFoundException(VacationErrorCode error, String message) {
        super(error, message);
    }
}
