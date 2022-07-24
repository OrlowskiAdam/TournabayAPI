package com.tournabay.api.payload;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class CreateMatchRequest {
    @NotNull(message = "Date is required!")
    private LocalDate date;
    @NotNull(message = "Time is required!")
    private LocalTime time;
    private Boolean isLive;
    private Long redParticipantId;
    private Long blueParticipantId;
    private Long redTeamId;
    private Long blueTeamId;
    private List<Long> refereeIds;
    private List<Long> commentatorIds;
    private List<Long> streamerIds;
}
