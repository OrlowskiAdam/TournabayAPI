package com.tournabay.api.payload;

import com.tournabay.api.model.ScoreType;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CreateTournamentRequest {
    private String name;
    private ScoreType scoreType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
