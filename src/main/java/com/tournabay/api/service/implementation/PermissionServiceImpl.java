package com.tournabay.api.service.implementation;

import com.tournabay.api.dto.PermissionDto;
import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.repository.PermissionRepository;
import com.tournabay.api.service.PermissionService;
import com.tournabay.api.service.StaffMemberService;
import com.tournabay.api.service.TournamentRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final TournamentRoleService tournamentRoleService;
    private final StaffMemberService staffMemberService;

    /**
     * "If the permission's id matches the id passed in, return the permission, otherwise throw an exception."
     *
     * @param id         The id of the permission to be found
     * @param tournament The tournament object that is being passed in from the controller.
     * @return Permission
     */
    public Permission findById(Long id, Tournament tournament) {
        return tournament.getPermissions()
                .stream()
                .filter(permission -> permission.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Permission not found"));
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
     * It creates a list of permissions for a tournament, based on the default permissions defined in the
     * `getDefaultPermissionAssignations()` function
     *
     * @return A list of permissions
     */
    @Transactional
    @SuppressWarnings("UnusedReturnValue")
    public List<Permission> createDefaultPermissions(List<TournamentRole> tournamentRoles) {
        Map<String, List<String>> defaultPermissionAssignations = getDefaultPermissionAssignations();
        List<Permission> permissions = new ArrayList<>();
        defaultPermissionAssignations.forEach((key, value) -> {
            List<TournamentRole> roles = tournamentRoleService.getRolesByName(value, tournamentRoles);
            Permission permission = Permission.builder()
                    .permissionName(key)
                    .permittedRoles(roles)
                    .permittedStaffMembers(new ArrayList<>())
                    .build();
            roles.forEach(role -> role.getPermissions().add(permission));
            permissions.add(permission);
        });
        return permissions;
    }

    /**
     * It updates a list of permissions, which are linked to a tournament, by updating the list of roles and staff members
     * that are allowed to use the permission
     *
     * @param permissionDtos A list of PermissionDto objects.
     * @param tournament     The tournament that the permission is for
     * @return A list of permissions.
     */
    @Transactional
    @Override
    public List<Permission> updatePermissions(List<PermissionDto> permissionDtos, Tournament tournament) {
        List<Permission> permissions = new ArrayList<>();
        for (PermissionDto permissionDto : permissionDtos) {
            Permission permission = this.findById(permissionDto.getId(), tournament);
            List<TournamentRole> tournamentRoles = tournamentRoleService.getAllById(permissionDto.getPermittedRolesId(), tournament);
            List<StaffMember> staffMembers = staffMemberService.getStaffMembersById(permissionDto.getPermittedStaffMembersId(), tournament);
            permission.getPermittedRoles().forEach(role -> role.getPermissions().remove(permission));
            permission.getPermittedStaffMembers().forEach(staffMember -> staffMember.getPermissions().remove(permission));
            permission.setPermittedRoles(tournamentRoles);
            permission.setPermittedStaffMembers(staffMembers);
            tournamentRoles.forEach(role -> role.getPermissions().add(permission));
            staffMembers.forEach(staffMember -> staffMember.getPermissions().add(permission));
            permissions.add(permission);
        }
        return permissionRepository.saveAll(permissions);
    }

    private Map<String, List<String>> getDefaultPermissionAssignations() {
        Map<String, List<String>> defaultPermissionAssignations = new HashMap<>();
        defaultPermissionAssignations.put("Roles", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("Staff", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("Permissions", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("Participants", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("Teams", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("Groups", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("QualificationRooms", Arrays.asList("Host", "Organizer"));
        defaultPermissionAssignations.put("QualificationResults", Arrays.asList("Host", "Organizer", "Referee"));
        defaultPermissionAssignations.put("Matches", Arrays.asList("Host", "Organizer", "Referee"));
        defaultPermissionAssignations.put("Mappool", Arrays.asList("Host", "Organizer", "Pooler"));
        defaultPermissionAssignations.put("Settings", Arrays.asList("Host", "Organizer"));
        return defaultPermissionAssignations;
    }

}
