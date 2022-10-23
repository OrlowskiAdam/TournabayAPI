package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectGroupType extends RuntimeException {
    public IncorrectGroupType(String message) {
        super(message);
    }

    public IncorrectGroupType(String message, Throwable cause) {
        super(message, cause);
    }
}
