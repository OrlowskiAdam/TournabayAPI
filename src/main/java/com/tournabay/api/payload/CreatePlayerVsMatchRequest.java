package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class CreatePlayerVsMatchRequest extends CreateMatchRequest {
    private Long redParticipantId;
    private Long blueParticipantId;
}
