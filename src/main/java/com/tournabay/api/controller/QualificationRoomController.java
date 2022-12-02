package com.tournabay.api.controller;

import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.model.qualifications.QualificationRoom;
import com.tournabay.api.payload.UpdateQualificationRoomRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.QualificationRoomService;
import com.tournabay.api.service.StaffMemberService;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import com.tournabay.api.payload.CreateQualificationRoomRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qualification-room")
public class QualificationRoomController {
    private final TournamentService tournamentService;
    private final UserService userService;
    private final QualificationRoomService qualificationRoomService;
    private final StaffMemberService staffMemberService;

    @GetMapping("/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationRooms')")
    public ResponseEntity<?> getQualificationRooms(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        return ResponseEntity.ok(qualificationRoomService.getQualificationRooms(tournament));
    }

    @PostMapping("/create/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationRooms')")
    public ResponseEntity<QualificationRoom> createQualificationRoom(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long tournamentId,
            @RequestBody CreateQualificationRoomRequest body
    ) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        QualificationRoom qualificationRoom = qualificationRoomService.createQualificationRoom(body.getStartDate(), tournament);
        return ResponseEntity.ok(qualificationRoom);
    }

    @PutMapping("/update/{qualificationRoomId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationRooms')")
    public ResponseEntity<QualificationRoom> updateQualificationRoom(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long qualificationRoomId,
            @PathVariable Long tournamentId,
            @RequestBody UpdateQualificationRoomRequest body
    ) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        QualificationRoom qualificationRoom = qualificationRoomService.getQualificationRoom(qualificationRoomId, tournament);
        qualificationRoom = qualificationRoomService.updateQualificationRoom(tournament, qualificationRoom, body);
        return ResponseEntity.ok(qualificationRoom);
    }

    @DeleteMapping("/remove/{qualificationRoomId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationRooms')")
    public ResponseEntity<List<QualificationRoom>> removeQualificationRoom(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long qualificationRoomId,
            @PathVariable Long tournamentId
    ) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        QualificationRoom qualificationRoom = qualificationRoomService.getQualificationRoom(qualificationRoomId, tournament);
        qualificationRoomService.removeQualificationRoom(qualificationRoom, tournament);
        List<QualificationRoom> qualificationRooms = qualificationRoomService.reassignSymbols(tournament.getQualificationRooms());
        return ResponseEntity.ok(qualificationRooms);
    }

    @PutMapping("/add-staff-member/{qualificationRoomId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationRooms')")
    public ResponseEntity<QualificationRoom> addStaffMember(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long qualificationRoomId,
            @PathVariable Long tournamentId
    ) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        QualificationRoom qualificationRoom = qualificationRoomService.getQualificationRoom(qualificationRoomId, tournament);
        User user = userService.getUserFromPrincipal(userPrincipal);
        StaffMember staffMember = staffMemberService.getStaffMemberByUser(user, tournament);
        qualificationRoom = qualificationRoomService.addStaffMember(qualificationRoom, staffMember);
        return ResponseEntity.ok(qualificationRoom);
    }

    @PutMapping("/remove-staff-member/{qualificationRoomId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationRooms')")
    public ResponseEntity<QualificationRoom> removeStaffMember(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long qualificationRoomId,
            @PathVariable Long tournamentId
    ) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        QualificationRoom qualificationRoom = qualificationRoomService.getQualificationRoom(qualificationRoomId, tournament);
        User user = userService.getUserFromPrincipal(userPrincipal);
        StaffMember staffMember = staffMemberService.getStaffMemberByUser(user, tournament);
        qualificationRoom = qualificationRoomService.removeStaffMember(qualificationRoom, staffMember);
        return ResponseEntity.ok(qualificationRoom);
    }
}
