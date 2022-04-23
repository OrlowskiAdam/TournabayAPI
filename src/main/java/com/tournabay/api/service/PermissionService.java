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

    public Permission createDefaultPermission(Tournament tournament, List<TournamentRole> tournamentRoles) {
        Permission permission = Permission.builder()
                .tournament(tournament)
                .canTournamentRoleManageRoles(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageRoles(new ArrayList<>())
                .canTournamentRoleManageStaffMembers(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageStaffMembers(new ArrayList<>())
                .canTournamentRoleManageAccess(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageAccess(new ArrayList<>())
                .build();
        return permissionRepository.save(permission);
    }

    public Permission updateRolesPermission(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageRoles(tournamentRoles);
        permission.setCanStaffMemberManageRoles(staffMembers);
        return permissionRepository.save(permission);
    }

    public Permission updateStaffPermission(Tournament tournament, List<TournamentRole> tournamentRoles, List<StaffMember> staffMembers) {
        Permission permission = permissionRepository.findByTournament(tournament).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCanTournamentRoleManageStaffMembers(tournamentRoles);
        permission.setCanStaffMemberManageStaffMembers(staffMembers);
        return permissionRepository.save(permission);
    }

    public void hasAccess(Tournament tournament, User user, List<TournamentRole> permittedRoles, List<StaffMember> permittedStaffMembers, StaffMember staffMembers) {
        if (tournament.getOwner().equals(user)) return;
        if (permittedRoles.stream().noneMatch(tournamentRole -> staffMembers.getTournamentRoles().contains(tournamentRole)))
            return;
        if (permittedStaffMembers.stream().noneMatch(permittedStaffMember -> permittedStaffMember.equals(staffMembers)))
            return;
        throw new ForbiddenException();
    }
}
