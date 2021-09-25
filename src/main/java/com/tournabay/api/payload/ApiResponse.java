package com.tournabay.api.payload;

import lombok.Data;

@Data
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
