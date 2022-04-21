package com.tournabay.api.controller;

import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.payload.AddStaffMemberRequest;
import com.tournabay.api.payload.RemoveStaffMembersRequest;
import com.tournabay.api.payload.UpdateStaffMemberRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.StaffMemberService;
import com.tournabay.api.service.TournamentRoleService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class StaffMemberController {
    private final TournamentService tournamentService;
    private final TournamentRoleService tournamentRoleService;
    private final StaffMemberService staffMemberService;

    @PostMapping("/add/{tournamentId}")
    @Secured("ROLE_USER")
    // TODO: Security Check
    // TODO: Roles should be taken from tournament, not directly from database
    public ResponseEntity<StaffMember> addStaffMember(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody @Valid AddStaffMemberRequest addStaffMemberRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<TournamentRole> tournamentRoles = tournamentRoleService.getAllById(addStaffMemberRequest.getTournamentRoleIds());
        StaffMember staffMember = staffMemberService.addStaffMember(addStaffMemberRequest.getOsuId(), tournament, tournamentRoles);
        return ResponseEntity.status(201).body(staffMember);
    }

    @DeleteMapping("/remove/{staffMemberId}/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> removeStaffMember(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @PathVariable Long staffMemberId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        StaffMember staffMember = staffMemberService.getStaffMemberById(staffMemberId, tournament);
        staffMemberService.deleteStaffMember(staffMember, tournament);
        return ResponseEntity.status(204).build();
    }

    @PostMapping("/remove/{tournamentId}")
    @Secured("ROLE_USER")
    // TODO: Security check
    public ResponseEntity<List<StaffMember>> removeStaffMembers(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody RemoveStaffMembersRequest removeStaffMembersRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<StaffMember> staffMembers = staffMemberService.getStaffMembersById(removeStaffMembersRequest.getStaffMemberIds(), tournament);
        List<StaffMember> deletedStaffMembers = staffMemberService.deleteStaffMembers(staffMembers);
        return ResponseEntity.ok(deletedStaffMembers);
    }

    @PatchMapping("/update/{tournamentId}")
    @Secured("ROLE_USER")
    // TODO: Security check
    public ResponseEntity<StaffMember> updateStaffMember(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody UpdateStaffMemberRequest updateStaffMemberRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<TournamentRole> tournamentRoles = tournamentRoleService.getAllById(updateStaffMemberRequest.getTournamentRoleIds(), tournament);
        StaffMember staffMember = staffMemberService.getStaffMemberById(updateStaffMemberRequest.getStaffMemberId(), tournament);
        StaffMember updatedStaffMember = staffMemberService.updateStaffMember(staffMember, tournamentRoles, updateStaffMemberRequest.getDiscordId(), tournament);
        return ResponseEntity.ok(updatedStaffMember);
    }
}
