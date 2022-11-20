package com.tournabay.api.dto;

import com.tournabay.api.model.Team;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class TeamBasedQualificationResultDto extends QualificationResultDto {
    private Team team;
    private List<TeamScoresDto> scores;
    private Double qualificationPoints;
}
