package com.tournabay.api.jackson.multiplayerLobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class MultiplayerLobbyProperty {
    @JsonProperty("match")
    private MultiplayerLobbyMatch match;
    @JsonProperty("games")
    private Collection<MultiplayerLobbyGames> games;
}
