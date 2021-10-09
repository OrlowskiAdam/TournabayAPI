package com.tournabay.api.payload;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateStaffMemberRequest {
    private Long staffMemberId;
    private String discordId;
    private List<Long> tournamentRoleIds;
}
