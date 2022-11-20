package com.tournabay.api.osu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {
    private Long id;
    private MatchEventDetail detail;
    private Long user_id;
    private MatchEventGame game;
}
