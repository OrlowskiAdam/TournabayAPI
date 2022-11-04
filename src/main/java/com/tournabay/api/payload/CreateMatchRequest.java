package com.tournabay.api.payload;

import com.tournabay.api.model.Stage;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public abstract class CreateMatchRequest {
    @NotNull(message = "Start date is required!")
    private LocalDateTime startDate;
    @NotNull(message = "Stage is required!")
    private Stage stage;
    private Long groupId;
    private Boolean isLive;
    private List<Long> refereesId;
    private List<Long> commentatorsId;
    private List<Long> streamersId;
}
