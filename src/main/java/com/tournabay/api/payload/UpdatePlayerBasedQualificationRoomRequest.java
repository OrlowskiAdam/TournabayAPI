package com.tournabay.api.payload;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdatePlayerBasedQualificationRoomRequest extends UpdateQualificationRoomRequest {
    private List<Long> participantIds;
}
