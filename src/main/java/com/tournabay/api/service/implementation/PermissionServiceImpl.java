package com.tournabay.api.service.implementation;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.repository.PermissionRepository;
import com.tournabay.api.service.PermissionService;
import com.tournabay.api.service.TournamentRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final TournamentRoleService tournamentRoleService;

    /**
     * "If the permission's id matches the id passed in, return the permission, otherwise throw an exception."
     *
     * @param id The id of the permission to be found
     * @param tournament The tournament object that is being passed in from the controller.
     * @return Permission
     */
    public Permission findById(Long id, Tournament tournament) {
        Permission permission = tournament.getPermission();
        if (permission.getId().equals(id)) {
            return permission;
        }
        throw new BadRequestException("Permission not found");
    }

    /**
     * Save the permission to the database.
     *
     * @param permission The permission object to be saved.
     * @return The permission object that was saved.
     */
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    /**
     * Create a new permission object with default roles and staff members assigned.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of all the tournament roles that exist in the tournament.
     * @return A Permission object
     */
    @SuppressWarnings("UnusedReturnValue")
    public Permission createDefaultPermission(Tournament tournament, List<TournamentRole> tournamentRoles) {
        Permission permission = Permission.builder()
                .tournament(tournament)
                .tournamentRolesRolesPermissions(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .staffMembersRolesPermissions(new ArrayList<>())
                .build();
        return permissionRepository.save(permission);
    }



}
