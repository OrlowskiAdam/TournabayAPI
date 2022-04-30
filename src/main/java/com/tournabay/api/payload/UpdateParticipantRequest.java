package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class UpdateParticipantRequest {
    private String discordId;
    private Long teamId;
}
