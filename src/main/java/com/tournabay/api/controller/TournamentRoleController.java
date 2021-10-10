package com.tournabay.api.controller;

import com.tournabay.api.dto.TournamentRoleRemovalDto;
import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.payload.CreateTournamentRoleRequest;
import com.tournabay.api.payload.UpdateTournamentRoleRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.StaffMemberService;
import com.tournabay.api.service.TournamentRoleService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/tournament-role")
public class TournamentRoleController {
    private final TournamentService tournamentService;
    private final TournamentRoleService tournamentRoleService;
    private final StaffMemberService staffMemberService;

    @PostMapping("/add/{tournamentId}")
    @Secured("ROLE_USER")
    // TODO: Security check
    public ResponseEntity<TournamentRole> addTournamentRole(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody CreateTournamentRoleRequest createTournamentRoleRequest) {
        if (createTournamentRoleRequest.getRoleName() == null || createTournamentRoleRequest.getRoleName().equals("")) throw new BadRequestException("Please specify role name!");
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        TournamentRole tournamentRole;
        if (createTournamentRoleRequest.getInherit() == null || createTournamentRoleRequest.getInherit().equals("")) {
            tournamentRole = tournamentRoleService.createRole(createTournamentRoleRequest.getRoleName(), createTournamentRoleRequest.getIsHidden(), tournament);
        } else {
            tournamentRole = tournamentRoleService.createRole(createTournamentRoleRequest.getRoleName(), createTournamentRoleRequest.getInherit(), createTournamentRoleRequest.getIsHidden(), tournament);
        }
        return ResponseEntity.status(201).body(tournamentRole);
    }

    @PatchMapping("/update/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<TournamentRole> updateTournamentRole(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody UpdateTournamentRoleRequest updateTournamentRoleRequest) {
        if (updateTournamentRoleRequest.getRoleName() == null || updateTournamentRoleRequest.getRoleName().equals("")) throw new BadRequestException("Please specify role name!");
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        TournamentRole tournamentRole = tournamentRoleService.getRoleById(updateTournamentRoleRequest.getRoleId(), tournament);
        TournamentRole updatedRole = tournamentRoleService.updateRole(tournamentRole, updateTournamentRoleRequest.getRoleName(), updateTournamentRoleRequest.getIsHidden());
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/remove/{roleId}/{tournamentId}")
    @Secured("ROLE_USER")
    @Transactional
    public ResponseEntity<TournamentRoleRemovalDto> removeTournamentRole(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long roleId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        TournamentRole tournamentRole = tournamentRoleService.getRoleById(roleId, tournament);
        List<StaffMember> associatedStaffMembers = staffMemberService.getStaffMembersByTournamentRole(tournamentRole, tournament);
        List<StaffMember> staffMembers = staffMemberService.disconnectTournamentRoleFromStaffMember(tournamentRole, associatedStaffMembers, tournament);
        TournamentRole removedRole = tournamentRoleService.removeRole(tournamentRole, associatedStaffMembers, tournament);
        return ResponseEntity.ok(new TournamentRoleRemovalDto(removedRole, staffMembers));
    }
}
