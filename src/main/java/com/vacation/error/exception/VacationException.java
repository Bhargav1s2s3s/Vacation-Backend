package com.vacation.error.exception;


import com.vacation.error.code.VacationErrorCode;
import lombok.Getter;

@Getter
public class VacationException extends RuntimeException {

    private final VacationErrorCode error;

    public VacationException(VacationErrorCode error, String message) {
        super(message);
        this.error = error;
    }
}
