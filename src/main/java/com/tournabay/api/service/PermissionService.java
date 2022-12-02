package com.tournabay.api.service;

import com.tournabay.api.dto.PermissionDto;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PermissionService {
    Permission findById(Long id, Tournament tournament);

    Permission save(Permission permission);

    @SuppressWarnings("UnusedReturnValue")
    List<Permission> createDefaultPermissions(List<TournamentRole> tournamentRoles);

    List<Permission> updatePermissions(List<PermissionDto> permissionDtos, Tournament tournament);
}
