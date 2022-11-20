package com.tournabay.api.payload;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateQualificationRoomRequest {
    private LocalDateTime startDate;
}
