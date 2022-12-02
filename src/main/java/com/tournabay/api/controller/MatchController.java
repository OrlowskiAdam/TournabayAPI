package com.tournabay.api.controller;

import com.tournabay.api.model.Match;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.*;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.MatchService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {
    private final TournamentService tournamentService;
    private final MatchService matchService;

    @PostMapping("/create-player-vs/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Matches')")
    public ResponseEntity<Match> createPlayerVsMatch(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody CreatePlayerVsMatchRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.createPlayerVsMatch(tournament, body);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/create-team-vs/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Matches')")
    public ResponseEntity<Match> createTeamVsMatch(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody CreateTeamVsMatchRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.createTeamVsMatch(tournament, body);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/update-player-vs/{matchId}/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Matches')")
    public ResponseEntity<Match> updatePlayerVsMatch(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long matchId, @PathVariable Long tournamentId, @RequestBody UpdatePlayerVsMatch body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.findById(tournament, matchId);
        match = matchService.updatePlayerVsMatch(tournament, match, body);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/update-team-vs/{matchId}/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Matches')")
    public ResponseEntity<Match> updateTeamVsMatch(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long matchId, @PathVariable Long tournamentId, @RequestBody UpdateTeamVsMatch body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.findById(tournament, matchId);
        match = matchService.updateTeamVsMatch(tournament, match, body);
        return ResponseEntity.ok(match);
    }

    @DeleteMapping("/delete/{matchId}/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Matches')")
    public ResponseEntity<Match> deleteMatch(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @PathVariable Long matchId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.removeMatchById(tournament, matchId);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/submit-result/{matchId}/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Matches')")
    public ResponseEntity<Match> submitResult(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @PathVariable Long matchId, @RequestBody MatchResultRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.findById(tournament, matchId);
        match = matchService.submitResult(tournament, match, body);
        return ResponseEntity.ok(match);
    }
}
