package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DiscordVerificationException extends RuntimeException {
    public DiscordVerificationException(String message) {
        super(message);
    }

    public DiscordVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}

