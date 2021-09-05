package com.tournabay.api.controller;

import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.payload.CreateTournamentRequest;
import com.tournabay.api.repository.UserRepository;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TournamentController {
    private final UserService userService;
    private final TournamentService tournamentService;

    @PostMapping("/tournament/create")
    @Secured("ROLE_USER")
    public ResponseEntity<Tournament> createTournament(@CurrentUser UserPrincipal userPrincipal, @RequestBody CreateTournamentRequest body) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.createTournament(body.getName(), body.getScoreType(), body.getStartDate(), body.getEndDate(), user);
        return ResponseEntity.ok(tournament);
    }
}
