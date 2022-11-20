package com.tournabay.api.osu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchScores {
    private Float accuracy;
    private Long id;
    private Long max_combo;
    private String mode;
    private Integer mode_int;
    private List<String> mods;
    private Boolean passed;
    private String rank;
    private Long score;
    private MatchScoresStatistics statistics;
    private Long user_id;
}
