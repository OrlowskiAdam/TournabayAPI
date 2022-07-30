package com.tournabay.api.controller;

import com.tournabay.api.model.Match;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.CreateMatchRequest;
import com.tournabay.api.service.MatchService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {
    private final TournamentService tournamentService;
    private final MatchService matchService;

    @PostMapping("/create/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Match> createMatch(@PathVariable Long tournamentId, @RequestBody @Valid CreateMatchRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.createMatch(tournament, body);
        return ResponseEntity.ok(match);
    }

    @DeleteMapping("/delete/{matchId}/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Match> deleteMatch(@PathVariable Long matchId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.deleteMatchById(matchId, tournament);
        return ResponseEntity.ok(match);
    }

}
