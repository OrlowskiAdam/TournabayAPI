package com.tournabay.api.controller;

import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.settings.TournamentSettings;
import com.tournabay.api.payload.UpdateRegistrationSettingsRequest;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.TournamentSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TournamentSettingsController {
    private final TournamentService tournamentService;
    private final TournamentSettingsService tournamentSettingsService;

    @PostMapping("/tournament/{tournamentId}/settings/registration")
    @Secured("ROLE_USER")
    public ResponseEntity<TournamentSettings> updateSettings(@PathVariable Long tournamentId, @RequestBody UpdateRegistrationSettingsRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        TournamentSettings tournamentSettings = tournamentSettingsService.updateRegistrationSettings(tournament, body);
        return ResponseEntity.ok(tournamentSettings);
    }
}
