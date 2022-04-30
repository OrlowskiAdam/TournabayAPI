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
                .canTournamentRoleManageParticipants(tournamentRoleService.getRolesByName(Arrays.asList("Host", "Organizer"), tournamentRoles))
                .canStaffMemberManageParticipants(new ArrayList<>())
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
