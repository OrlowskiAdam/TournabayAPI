package com.tournabay.api.controller;

import com.tournabay.api.model.Participant;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.service.ParticipantService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final TournamentService tournamentService;
    private final ParticipantService participantService;

    /**
     * Add a participant to a tournament if the user has the permission to do so.
     * <p>
     * The `@Secured` annotation is used to ensure that the user is logged in
     *
     * @param osuId        The osu! user id of the user you want to add to the tournament.
     * @param tournamentId The id of the tournament you want to add the participant to.
     * @return A ResponseEntity with the added participant.
     */
    @Secured("ROLE_USER")
    @PostAuthorize("hasPermission(#tournamentId, 'ManageParticipants')")
    @PostMapping("/add/{osuId}/{tournamentId}")
    public ResponseEntity<Participant> addParticipant(@PathVariable Long osuId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Participant participant = participantService.getByOsuId(osuId, tournament);
        Participant addedParticipant = participantService.addParticipant(tournament, participant);
        return ResponseEntity.ok(addedParticipant);
    }

    /**
     * If the user has the role of ROLE_USER, and if the user has the permission to manage participants, then delete the
     * participant.
     *
     * @param participantId The id of the participant to delete
     * @param tournamentId  The id of the tournament that the participant is in.
     * @return A ResponseEntity with a status code of 200.
     */
    @Secured("ROLE_USER")
    @PostAuthorize("hasPermission(#tournamentId, 'ManageParticipants')")
    @PostMapping("/delete/{participantId}/{tournamentId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long participantId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        participantService.deleteById(participantId, tournament);
        return ResponseEntity.ok().build();
    }

    // TODO: Update participant endpoint
//    @Secured("ROLE_USER")
//    @PatchMapping("/update/{participantId}/{tournamentId}")
//    public ResponseEntity<Participant> updateParticipant(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long participantId, @PathVariable Long tournamentId, @RequestBody UpdateParticipantRequest body) {
//        Tournament tournament = tournamentService.getTournamentById(tournamentId);
//        User user = userService.getUserFromPrincipal(userPrincipal);
//        permissionService.hasAccess(
//                tournament,
//                user,
//                tournament.getPermission().getCanTournamentRoleManageParticipants(),
//                tournament.getPermission().getCanStaffMemberManageParticipants()
//        );
//        Participant participant = participantService.getById(participantId, tournament);
//        Participant updatedParticipant = participantService.updateParticipant(participant, body, tournament);
//        return ResponseEntity.ok(updatedParticipant);
//    }
}
