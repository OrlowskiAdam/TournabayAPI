package com.tournabay.api.payload;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public abstract class UpdateMatchRequest {
    @NotNull(message = "Start date is required!")
    private LocalDateTime startDate;
    @NotNull(message = "'Is live' value cannot be null!")
    private Boolean isLive;
    private List<Long> refereesId;
    private List<Long> commentatorsId;
    private List<Long> streamersId;
    @NotNull(message = "Referees limit cannot be null!")
    @Min(value = 0, message = "Referees limit must be greater than 0!")
    private Integer refereesLimit;
    @NotNull(message = "Commentators limit cannot be null!")
    @Min(value = 0, message = "Commentators limit must be greater than 0!")
    private Integer commentatorsLimit;
    @NotNull(message = "Streamers limit cannot be null!")
    @Min(value = 0, message = "Streamers limit must be greater than 0!")
    private Integer streamersLimit;
}
