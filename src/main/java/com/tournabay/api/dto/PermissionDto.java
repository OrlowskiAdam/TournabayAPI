package com.tournabay.api.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PermissionDto {
    private Long id;
    private List<Long> permittedRolesId;
    private List<Long> permittedStaffMembersId;
}
