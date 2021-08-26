package com.tournabay.api.controller;

import com.tournabay.api.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TournamentController {

    @PostMapping("/tournament/create")
    public ResponseEntity<Tournament> createTournament() {
        return ResponseEntity.ok(null);
    }
}
