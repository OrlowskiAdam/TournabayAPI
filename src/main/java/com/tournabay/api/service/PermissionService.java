package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.RolePermission;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public List<Permission> saveAll(List<Permission> permissions) {
        return permissionRepository.saveAll(permissions);
    }

    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }

    public Permission createRolePermission(boolean canRead, boolean canWrite, TournamentRole tournamentRole) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRead(canRead);
        rolePermission.setWrite(canWrite);
        rolePermission.setTournamentRole(tournamentRole);
        return rolePermission;
    }
}
