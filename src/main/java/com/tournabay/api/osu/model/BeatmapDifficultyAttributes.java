package com.tournabay.api.osu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapDifficultyAttributes {
    private Integer max_combo;
    private Float star_rating;
    private Float aim_difficulty;
    private Float approach_rate;
    private Float flashlight_difficulty;
    private Float overall_difficulty;
    private Float slider_factor;
    private Float speed_difficulty;
}
