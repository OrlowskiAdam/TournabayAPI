package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedTournamentType extends RuntimeException {
    public UnsupportedTournamentType(String message) {
        super(message);
    }

    public UnsupportedTournamentType(String message, Throwable cause) {
        super(message, cause);
    }
}
