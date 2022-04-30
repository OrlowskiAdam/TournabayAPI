package com.tournabay.api.controller;

import com.tournabay.api.model.Participant;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.payload.DeleteParticipantsRequest;
import com.tournabay.api.payload.SetParticipantsStatusRequest;
import com.tournabay.api.payload.UpdateParticipantRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.ParticipantService;
import com.tournabay.api.service.PermissionService;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final UserService userService;
    private final TournamentService tournamentService;
    private final ParticipantService participantService;
    private final PermissionService permissionService;

    @Secured("ROLE_USER")
    @PostMapping("/add/{osuId}/{tournamentId}")
    // TODO: Security check
    public ResponseEntity<Participant> addParticipant(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long osuId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageParticipants(),
                tournament.getPermission().getCanStaffMemberManageParticipants()
        );
        Participant participant = participantService.getByOsuId(osuId);
        Participant addedParticipant = tournamentService.addParticipant(tournament, participant);
        return ResponseEntity.ok(addedParticipant);
    }

    @Secured("ROLE_USER")
    @PostMapping("/delete/{participantId}/{tournamentId}")
    public ResponseEntity<Void> deleteParticipant(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long participantId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageParticipants(),
                tournament.getPermission().getCanStaffMemberManageParticipants()
        );
        participantService.deleteById(participantId, tournament);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PatchMapping("/update/{participantId}/{tournamentId}")
    public ResponseEntity<Participant> updateParticipant(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long participantId, @PathVariable Long tournamentId, @RequestBody UpdateParticipantRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageParticipants(),
                tournament.getPermission().getCanStaffMemberManageParticipants()
        );
        Participant participant = participantService.getById(participantId, tournament);
        Participant updatedParticipant = participantService.updateParticipant(participant, body, tournament);
        return ResponseEntity.ok(updatedParticipant);
    }
}
