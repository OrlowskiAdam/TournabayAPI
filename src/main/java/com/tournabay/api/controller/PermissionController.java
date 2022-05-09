package com.tournabay.api.controller;

import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.payload.PermissionPayload;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.PermissionService;
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
    private final UserService userService;
    private final PermissionService permissionService;

    /**
     * Update the roles permissions for a tournament.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param tournamentId The id of the tournament you want to update the roles for.
     * @param permissionPayload This is the payload that is sent to the endpoint. It contains the tournamentRoles and
     * staffMembers that are to be updated.
     * @return A ResponseEntity with the updated Permission object.
     */
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

    /**
     * Update the staff permissions for a tournament.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param tournamentId The id of the tournament you want to update the permissions for.
     * @param permissionPayload This is the payload that is sent to the server. It contains the tournament roles and staff
     * members that are to be updated.
     * @return A ResponseEntity with the updated Permission object.
     */
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

    /**
     * Update the access permissions for a tournament.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param tournamentId The id of the tournament you want to update the permissions for.
     * @param permissionPayload This is the payload that is sent to the endpoint. It contains the tournament roles and
     * staff members that are allowed to access the tournament.
     * @return A ResponseEntity with the updated Permission object.
     */
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

    /**
     * Update the participants permissions for a tournament.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param tournamentId The id of the tournament you want to update the permissions for.
     * @param permissionPayload This is the payload that is sent to the endpoint. It contains the tournament roles and
     * staff members that are allowed to access the tournament.
     * @return A ResponseEntity with the updated Permission object.
     */
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

    /**
     * Update the settings permissions for a tournament.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param tournamentId The id of the tournament you want to update the permissions for.
     * @param permissionPayload This is the payload that is sent to the endpoint. It contains the tournament roles and
     * staff members that are allowed to access the tournament.
     * @return A ResponseEntity with the updated Permission object.
     */
    @PatchMapping("/settings/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Permission> updateSettingsPermission(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody PermissionPayload permissionPayload) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageRoles(),
                tournament.getPermission().getCanStaffMemberManageRoles()
        );
        Permission updatedPermission = permissionService.updateParticipantsSettings(tournament, permissionPayload.getTournamentRoles(), permissionPayload.getStaffMembers());
        return ResponseEntity.ok(updatedPermission);
    }
}
