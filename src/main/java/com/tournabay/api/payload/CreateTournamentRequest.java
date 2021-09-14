package com.tournabay.api.payload;

import com.tournabay.api.model.GameMode;
import com.tournabay.api.model.ScoreType;
import com.tournabay.api.model.TeamFormat;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CreateTournamentRequest {
    private String name;
    private ScoreType scoreType;
    private GameMode gameMode;
    private TeamFormat teamFormat;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
