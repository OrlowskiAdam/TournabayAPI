package com.tournabay.api.payload;

import com.tournabay.api.dto.PermissionDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PermissionsRequest {
    List<PermissionDto> permissionDtos;
}
