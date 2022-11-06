package com.tournabay.api.payload;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UpdateQualificationRoomRequest {
    private LocalDateTime startTime;
    private List<Long> staffMemberIds;
}
