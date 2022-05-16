package com.tournabay.api.payload;

import com.tournabay.api.model.Seed;
import com.tournabay.api.model.TeamStatus;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class CreateTeamRequest {
    @NotNull(message = "Team name cannot be empty")
    @NotBlank(message = "Team name cannot be empty")
    private String name;
    private List<Long> participantIds;
    @NotNull(message = "Seed cannot be empty")
    private Seed seed;
    @NotNull(message = "Status cannot be empty")
    private TeamStatus status;
}
