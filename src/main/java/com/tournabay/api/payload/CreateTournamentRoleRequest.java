package com.tournabay.api.payload;

import lombok.Getter;

@Getter
public class CreateTournamentRoleRequest {
    private String roleName;
    private Long inherit;
}
