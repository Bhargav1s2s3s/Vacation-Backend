package com.vacation.common.error.exception;


import com.vacation.common.error.code.VacationErrorCode;
import lombok.Getter;

@Getter
public class VacationException extends RuntimeException {

    private final VacationErrorCode error;

    public VacationException(VacationErrorCode error, String message) {
        super(message);
        this.error = error;
    }
}
