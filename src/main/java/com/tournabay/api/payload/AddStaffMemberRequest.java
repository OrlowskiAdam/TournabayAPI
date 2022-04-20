package com.tournabay.api.payload;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class AddStaffMemberRequest {
    @NotNull(message = "Osu ID cannot be empty!")
    private Long osuId;
    private List<Long> tournamentRoleIds;
}
