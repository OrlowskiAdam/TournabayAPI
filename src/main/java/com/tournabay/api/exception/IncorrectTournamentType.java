package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectTournamentType extends RuntimeException {
    public IncorrectTournamentType(String message) {
        super(message);
    }

    public IncorrectTournamentType(String message, Throwable cause) {
        super(message, cause);
    }
}
