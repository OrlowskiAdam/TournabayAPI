package com.tournabay.api.jackson.multiplayerLobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;

@Getter
public class MultiplayerLobbyGames {

    @JsonProperty("game_id")
    private Long gameId;
    @JsonProperty("beatmap_id")
    private Long beatmapId;
    @JsonProperty("mods")
    private Long mods;
    @JsonProperty("scores")
    private Collection<MultiplayerLobbyScores> scores;

}
