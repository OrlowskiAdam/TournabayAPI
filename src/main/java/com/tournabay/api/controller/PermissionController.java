package com.tournabay.api.controller;

import com.tournabay.api.exception.ForbiddenException;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.payload.PermissionPayload;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.PermissionService;
import com.tournabay.api.service.StaffMemberService;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {
    private final TournamentService tournamentService;
    private final StaffMemberService staffMemberService;
    private final UserService userService;
    private final PermissionService permissionService;

    @PatchMapping("/roles/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Permission> updateRolesPermission(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody PermissionPayload permissionPayload) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageRoles(),
                tournament.getPermission().getCanStaffMemberManageRoles()
        );
        Permission updatedPermission = permissionService.updateRolesPermission(tournament, permissionPayload.getTournamentRoles(), permissionPayload.getStaffMembers());
        return ResponseEntity.ok(updatedPermission);
    }

    @PatchMapping("/staff/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Permission> updateStaffPermission(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody PermissionPayload permissionPayload) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageRoles(),
                tournament.getPermission().getCanStaffMemberManageRoles()
        );
        Permission updatedPermission = permissionService.updateStaffPermission(tournament, permissionPayload.getTournamentRoles(), permissionPayload.getStaffMembers());
        return ResponseEntity.ok(updatedPermission);
    }

    @PatchMapping("/access/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Permission> updateAccessPermission(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody PermissionPayload permissionPayload) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageRoles(),
                tournament.getPermission().getCanStaffMemberManageRoles()
        );
        Permission updatedPermission = permissionService.updateAccessPermission(tournament, permissionPayload.getTournamentRoles(), permissionPayload.getStaffMembers());
        return ResponseEntity.ok(updatedPermission);
    }

    @PatchMapping("/participants/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Permission> updateParticipantsPermission(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody PermissionPayload permissionPayload) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageRoles(),
                tournament.getPermission().getCanStaffMemberManageRoles()
        );
        Permission updatedPermission = permissionService.updateParticipantsPermission(tournament, permissionPayload.getTournamentRoles(), permissionPayload.getStaffMembers());
        return ResponseEntity.ok(updatedPermission);
    }
}
