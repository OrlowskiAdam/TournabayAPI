package com.tournabay.api.jackson.multiplayerLobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MultiplayerLobbyScores {

    @JsonProperty("slot")
    private int slot;
    @JsonProperty("team")
    private int team;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("score")
    private Long score;
    @JsonProperty("maxcombo")
    private int maxCombo;
    @JsonProperty("pass")
    private int pass;
}
