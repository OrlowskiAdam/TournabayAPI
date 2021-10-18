package com.tournabay.api.payload;

import com.tournabay.api.model.Permission;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdatePagePermissionsRequest {
    private Long pageId;
    private List<Permission> permissions;
}
