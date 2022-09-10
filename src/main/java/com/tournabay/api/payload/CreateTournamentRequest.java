package com.tournabay.api.payload;

import com.tournabay.api.model.GameMode;
import com.tournabay.api.model.ScoreType;
import com.tournabay.api.model.Stage;
import com.tournabay.api.model.TeamFormat;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
public class CreateTournamentRequest {
    @NotNull(message = "Tournament name cannot be null!")
    @NotEmpty(message = "Tournament name cannot be empty!")
    private String name;
    private ScoreType scoreType;
    private GameMode gameMode;
    private TeamFormat teamFormat;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Stage maxStage;
}
