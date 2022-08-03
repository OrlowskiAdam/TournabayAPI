package com.tournabay.api.controller;

import com.tournabay.api.model.Settings;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.service.SettingsService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TournamentSettingsController {
    private final TournamentService tournamentService;
    private final SettingsService settingsService;

    /**
     * If the user has permission to update the settings, update the settings.
     *
     * The first thing we do is get the tournament and the user. We then check if the user has permission to update the
     * settings. If they do, we update the settings
     *
     * information.
     * @param tournamentId The id of the tournament you want to update the settings for.
     * @param body The body of the request. This is the object with new tournament settings.
     * @return A ResponseEntity with the updated settings.
     */
    @PatchMapping("/tournament/{tournamentId}/settings")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'ManageSettings')")
    public ResponseEntity<Settings> updateSettings(@PathVariable Long tournamentId, @RequestBody Settings body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Settings settings = settingsService.updateSettings(tournament, body);
        return ResponseEntity.ok(settings);
    }
}
