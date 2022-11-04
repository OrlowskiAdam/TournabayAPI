package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class CreateTeamVsMatchRequest extends CreateMatchRequest {
    private Long redTeamId;
    private Long blueTeamId;
}
