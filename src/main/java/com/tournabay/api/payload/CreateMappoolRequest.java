package com.tournabay.api.payload;

import com.tournabay.api.model.Stage;
import lombok.Getter;

@Getter
public class CreateMappoolRequest {
    private String name;
    private Stage stage;
}
