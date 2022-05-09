package com.tournabay.api.service;

import com.tournabay.api.exception.ForbiddenException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final TournamentRoleService tournamentRoleService;

    /**
     * Create a new permission object with default roles and staff members assigned.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of all the tournament roles that exist in the tournament.
     * @return A Permission object
     */
    public Permission createDefaultPermission(Tournament tournament, List<TournamentRole> tournamentRoles) {
        Permission permission = Permission.builder()
                .tournament(tournament)
                .canTournamentRoleManageRoles(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageRoles(new ArrayList<>())
                .canTournamentRoleManageStaffMembers(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageStaffMembers(new ArrayList<>())
                .canTournamentRoleManageAccess(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageAccess(new ArrayList<>())
                .canTournamentRoleManageParticipants(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageParticipants(new ArrayList<>())
                .canTournamentRoleManageTeams(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageTeams(new ArrayList<>())
                .build();
        return permissionRepository.save(permission);
    }

    /**
     * Update the roles that can manage roles for a tournament.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of tournament roles that can manage roles
     * @param staffMembers A list of staff members that can manage roles.
     * @return A Permission object
     */
    public Permission updateRolesPermission(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageRoles(tournamentRoles);
        permission.setCanStaffMemberManageRoles(staffMembers);
        return permissionRepository.save(permission);
    }

    /**
     * Update the permission for a tournament to allow the given tournament roles to manage staff members, and allow the
     * given staff members to manage staff members.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of TournamentRoles that can manage staff members.
     * @param staffMembers A list of staff members that can manage staff members.
     * @return A Permission object
     */
    public Permission updateStaffPermission(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageStaffMembers(tournamentRoles);
        permission.setCanStaffMemberManageStaffMembers(staffMembers);
        return permissionRepository.save(permission);
    }

    /**
     * Update the access permission of a tournament.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of TournamentRoles that can manage access to the tournament.
     * @param staffMembers A list of staff members that can manage access to the tournament.
     * @return A Permission object
     */
    public Permission updateAccessPermission(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageAccess(tournamentRoles);
        permission.setCanStaffMemberManageAccess(staffMembers);
        return permissionRepository.save(permission);
    }

    /**
     * Update the participants permission of the given tournament with the given tournament roles and staff members.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of tournament roles that can manage participants.
     * @param staffMembers A list of staff members that can manage participants.
     * @return A Permission object
     */
    public Permission updateParticipantsPermission(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageParticipants(tournamentRoles);
        permission.setCanStaffMemberManageParticipants(staffMembers);
        return permissionRepository.save(permission);
    }

    /**
     * Update the settings permission of the given tournament with the given tournament roles and staff members.
     *
     * @param tournament The tournament that the permission is for
     * @param tournamentRoles A list of tournament roles that can manage settings.
     * @param staffMembers A list of staff members that can manage the tournament settings.
     * @return A Permission object
     */
    public Permission updateParticipantsSettings(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageSettings(tournamentRoles);
        permission.setCanStaffMemberManageSettings(staffMembers);
        return permissionRepository.save(permission);
    }

    /**
     * If the user is the owner of the tournament, or if the user is a staff member of the tournament with a permitted
     * role, or if the user is a staff member of the tournament that is in the list of permitted staff members, then the
     * user has access.
     *
     * @param tournament The tournament that the user is trying to access
     * @param user The user that is trying to access the resource
     * @param permittedRoles A list of TournamentRoles that the user must have in order to have access to the resource.
     * @param permittedStaffMembers A list of staff members that are allowed to access the resource.
     */
    public void hasAccess(Tournament tournament, User user, List<TournamentRole> permittedRoles, List<StaffMember> permittedStaffMembers) {
        if (tournament.getOwner().getId().equals(user.getId())) return;
        StaffMember staffMember = tournament.getStaffMembers()
                .stream()
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(ForbiddenException::new);
        if (permittedRoles.stream().noneMatch(tournamentRole -> staffMember.getTournamentRoles().contains(tournamentRole)))
            return;
        if (permittedStaffMembers.stream().noneMatch(permittedStaffMember -> permittedStaffMember.equals(staffMember)))
            return;
        throw new ForbiddenException();
    }
}
