package com.tournabay.api.jackson.multiplayerLobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class MultiplayerLobbyProperty {
    @JsonProperty("match")
    private MultiplayerLobbyMatch match;
    @JsonProperty("games")
    private Collection<MultiplayerLobbyGames> games;
}
