package com.tournabay.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectQualificationRoomType extends RuntimeException {
    public IncorrectQualificationRoomType(String message) {
        super(message);
    }

    public IncorrectQualificationRoomType(String message, Throwable cause) {
        super(message, cause);
    }
}
