package com.tournabay.api.osu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultiplayerLobbyData {
    private MatchData match;
    private List<MatchEvent> events;
    private List<OsuUser> users;
}
