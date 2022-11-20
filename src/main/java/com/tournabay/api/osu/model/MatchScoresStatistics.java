package com.tournabay.api.osu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoresStatistics {
    private Long count_100;
    private Long count_300;
    private Long count_50;
    private Long count_geki;
    private Long count_katu;
    private Long count_miss;
}
