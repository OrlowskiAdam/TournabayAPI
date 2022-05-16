package com.tournabay.api.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    private String message;
    private Object object;
}
