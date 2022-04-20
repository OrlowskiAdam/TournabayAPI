package com.tournabay.api.payload;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class UpdateStaffMemberRequest {
    @NotNull(message = "Staff member ID is null!")
    private Long staffMemberId;
    private String discordId;
    private List<Long> tournamentRoleIds;
}
