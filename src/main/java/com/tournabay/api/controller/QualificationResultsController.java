package com.tournabay.api.controller;

import com.tournabay.api.dto.QualificationResultDto;
import com.tournabay.api.model.*;
import com.tournabay.api.model.qualifications.QualificationRoom;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import com.tournabay.api.osu.model.MultiplayerLobbyData;
import com.tournabay.api.payload.UpdateParticipantQualificationScoresRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qualification-results")
public class QualificationResultsController {
    private final QualificationResultsService qualificationResultsService;
    private final UserService userService;
    private final TournamentService tournamentService;
    private final QualificationRoomService qualificationRoomService;
    private final TeamService teamService;
    private final ParticipantService participantService;

    @PostMapping("/submit/lobby/{lobbyId}/room/{roomId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationResults')")
    public ResponseEntity<List<QualificationResult>> submitQualificationResults(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long lobbyId,
            @PathVariable Long roomId,
            @PathVariable Long tournamentId
    ) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        QualificationRoom qualificationRoom = qualificationRoomService.getQualificationRoom(roomId, tournament);
        MultiplayerLobbyData multiplayerLobbyData = qualificationResultsService.getDataFromOsuApi(lobbyId, user);
        List<QualificationResult> results = qualificationResultsService.submitQualificationScores(multiplayerLobbyData, qualificationRoom, tournament);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationResults')")
    public ResponseEntity<List<?>> getQualificationResults(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament instanceof TeamBasedTournament) {
            List<QualificationResultDto> results = qualificationResultsService.getTeamBasedQualificationResults(tournament);
            return ResponseEntity.ok(results);
        }
        throw new NotYetImplementedException();
    }

    @GetMapping("/tournament/{tournamentId}/team/{teamId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationResults')")
    public ResponseEntity<List<QualificationResult>> getQualificationResultsForTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Team team = teamService.getById(teamId, tournament);
        if (tournament instanceof TeamBasedTournament) {
            List<QualificationResult> results = qualificationResultsService.getQualificationResultsByTeam(team, tournament);
            return ResponseEntity.ok(results);
        }
        throw new NotYetImplementedException();
    }

    @PutMapping("/update/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationResults')")
    public ResponseEntity<List<QualificationResultDto>> updateQualificationResultsForTeam(@PathVariable Long tournamentId, @RequestBody UpdateParticipantQualificationScoresRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<QualificationResultDto> results = qualificationResultsService.updateQualificationResults(body.getQualificationResults(), tournament);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/delete/tournament/{tournamentId}/team/{teamId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationResults')")
    public ResponseEntity<?> deleteQualificationResultsForTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Team team = teamService.getById(teamId, tournament);
        List<QualificationResultDto> qualificationResultDtos = qualificationResultsService.deleteQualificationResultsByTeam(team, tournament);
        return ResponseEntity.ok(qualificationResultDtos);
    }

    @DeleteMapping("/delete/tournament/{tournamentId}/participant/{participantId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'QualificationResults')")
    public ResponseEntity<List<?>> deleteQualificationResultsForParticipant(@PathVariable Long tournamentId, @PathVariable Long participantId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Participant participant = participantService.getById(participantId, tournament);
        List<QualificationResultDto> qualificationResultDtos = qualificationResultsService.deleteQualificationResultsByParticipant(participant, tournament);
        return ResponseEntity.ok(qualificationResultDtos);
    }
}
