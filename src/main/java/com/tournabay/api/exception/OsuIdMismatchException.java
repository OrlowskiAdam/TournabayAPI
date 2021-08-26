package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OsuIdMismatchException extends RuntimeException{
    public OsuIdMismatchException(String message) {
        super(message);
    }

    public OsuIdMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
