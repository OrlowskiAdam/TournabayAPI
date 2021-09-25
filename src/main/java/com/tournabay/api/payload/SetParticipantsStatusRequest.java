package com.tournabay.api.payload;

import com.tournabay.api.model.ParticipantStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class SetParticipantsStatusRequest {
    private List<Long> participantIds;
    private ParticipantStatus status;
}
