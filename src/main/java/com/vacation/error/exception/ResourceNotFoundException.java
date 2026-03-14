package com.vacation.error.exception;

import com.vacation.error.code.VacationErrorCode;

public class ResourceNotFoundException extends VacationException {
    public ResourceNotFoundException(VacationErrorCode error, String message) {
        super(error, message);
    }
}
