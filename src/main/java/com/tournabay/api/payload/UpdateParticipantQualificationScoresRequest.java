package com.tournabay.api.payload;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateParticipantQualificationScoresRequest {
    List<NewQualificationScore> qualificationResults;
}
