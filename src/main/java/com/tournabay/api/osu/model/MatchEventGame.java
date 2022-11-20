package com.tournabay.api.osu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchEventGame {
    private Long id;
    private String mode;
    private Integer mode_int;
    private String scoring_type;
    private String team_type;
    private List<String> mods;
    private OsuBeatmap beatmap;
    private List<MatchScores> scores;
}
