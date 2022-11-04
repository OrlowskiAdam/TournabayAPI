package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class UpdateTeamVsMatch extends UpdateMatchRequest {
    private Long redTeamId;
    private Long blueTeamId;
}
