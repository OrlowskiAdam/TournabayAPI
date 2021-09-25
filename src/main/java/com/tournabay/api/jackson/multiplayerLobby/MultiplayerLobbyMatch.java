package com.tournabay.api.jackson.multiplayerLobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MultiplayerLobbyMatch {

    @JsonProperty("match_id")
    private Long matchId;
    @JsonProperty("name")
    private String lobbyName;
}
