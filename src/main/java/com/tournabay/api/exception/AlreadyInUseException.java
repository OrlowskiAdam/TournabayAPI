package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyInUseException extends RuntimeException {
    public AlreadyInUseException(String message) {
        super(message);
    }

    public AlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
