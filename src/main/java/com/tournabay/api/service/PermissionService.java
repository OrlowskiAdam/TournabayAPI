package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.RolePermission;
import com.tournabay.api.model.Tournament;
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

    public Permission findPermissionById(Long id) {
        return permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }

    public RolePermission createRolePermission(boolean canRead, boolean canWrite, TournamentRole tournamentRole) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRead(canRead);
        rolePermission.setWrite(canWrite);
        rolePermission.setTournamentRole(tournamentRole);
        return rolePermission;
    }

    public List<Permission> updatePermissions(List<Permission> oldPermissions, List<Permission> newPermissions) {
        for (Permission o : oldPermissions) {
            for (Permission n : newPermissions) {
                if (o.getId().equals(n.getId())) {
                    o.setRead(n.getRead());
                    o.setWrite(n.getWrite());
                    if (!o.getRead()) o.setWrite(false);
                }
            }
        }
        return permissionRepository.saveAll(oldPermissions);
    }

    public Permission updateRolePermission(Long permissionId, boolean read, boolean write) {
        Permission permission = findPermissionById(permissionId);
        permission.setRead(read);
        permission.setWrite(write);
        return permission;
    }
}
