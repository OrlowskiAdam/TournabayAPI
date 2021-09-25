package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OsuUserNotFound extends RuntimeException {
    public OsuUserNotFound(String message) {
        super(message);
    }

    public OsuUserNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
