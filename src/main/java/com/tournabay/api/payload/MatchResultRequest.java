package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class MatchResultRequest {
    private Long redScore;
    private Long blueScore;
    private String lobbyLink;
}
