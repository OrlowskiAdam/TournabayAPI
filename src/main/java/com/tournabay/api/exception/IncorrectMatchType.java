package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectMatchType extends RuntimeException {
    public IncorrectMatchType(String message) {
        super(message);
    }

    public IncorrectMatchType(String message, Throwable cause) {
        super(message, cause);
    }
}
