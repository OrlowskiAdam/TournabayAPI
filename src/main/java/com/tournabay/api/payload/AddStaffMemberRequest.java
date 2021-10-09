package com.tournabay.api.payload;

import lombok.Getter;

import java.util.List;

@Getter
public class AddStaffMemberRequest {
    private Long osuId;
    private List<Long> tournamentRoleIds;
}
