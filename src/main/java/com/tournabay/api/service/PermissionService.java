package com.tournabay.api.service;

import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
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
}
