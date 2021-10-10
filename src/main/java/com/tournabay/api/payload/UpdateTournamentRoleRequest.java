package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class UpdateTournamentRoleRequest {
    private Long roleId;
    private String roleName;
    private Boolean isHidden;
}
