package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class UpdatePlayerVsMatch extends UpdateMatchRequest {
    private Long redParticipantId;
    private Long blueParticipantId;
}
