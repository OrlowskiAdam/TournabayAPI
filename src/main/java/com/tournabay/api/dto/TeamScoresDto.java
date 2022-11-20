package com.tournabay.api.dto;

import com.tournabay.api.model.beatmap.Beatmap;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TeamScoresDto {
    private Beatmap beatmap;
    private Double averageAccuracy;
    private Double averageScore;
    private Double qualificationPoints;
}
