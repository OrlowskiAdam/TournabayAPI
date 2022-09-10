package com.tournabay.api.controller;

import com.tournabay.api.model.Mappool;
import com.tournabay.api.model.Stage;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.CreateMappoolRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.MappoolService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mappool")
public class MappoolController {
    private final TournamentService tournamentService;
    private final MappoolService mappoolService;

    @GetMapping("/all/{tournamentId}")
    public ResponseEntity<List<Mappool>> getAllMappools(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<Mappool> tournamentMappools = mappoolService.findAllByTournament(tournament);
        return ResponseEntity.ok(tournamentMappools);
    }

    @PostMapping("/create/{tournamentId}")
    public ResponseEntity<Mappool> createMappool(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody CreateMappoolRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Mappool mappool = mappoolService.createMappool(tournament, body.getStage(), body.getName());
        return ResponseEntity.ok(mappool);
    }
}
