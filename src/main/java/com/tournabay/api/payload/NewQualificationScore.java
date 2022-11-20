package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class NewQualificationScore {
    private Long participantScoreId;
    private Long newScore;
    private Double newAccuracy;
}
