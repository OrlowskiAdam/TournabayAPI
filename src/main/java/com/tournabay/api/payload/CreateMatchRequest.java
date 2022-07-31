package com.tournabay.api.payload;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateMatchRequest {
    @NotNull(message = "Start date is required!")
    private LocalDateTime startDate;
    private Boolean isLive;
    private Long redParticipantId;
    private Long blueParticipantId;
    private Long redTeamId;
    private Long blueTeamId;
    private List<Long> refereesId;
    private List<Long> commentatorsId;
    private List<Long> streamersId;
}
