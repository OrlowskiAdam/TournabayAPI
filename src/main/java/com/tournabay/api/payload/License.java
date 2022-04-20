package com.tournabay.api.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class License {
    private LocalDateTime ldt;
}
