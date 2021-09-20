package com.tournabay.api.controller;

import com.tournabay.api.model.Participant;
import com.tournabay.api.model.PlayerBasedTournament;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.payload.CreateTournamentRequest;
import com.tournabay.api.repository.ParticipantRepository;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TournamentController {
    private final UserService userService;
    private final TournamentService tournamentService;

    //TODO: Delete
    private final ParticipantRepository participantRepository;

    @GetMapping("/tournament/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/tournament/create")
    @Secured("ROLE_USER")
    public ResponseEntity<Tournament> createTournament(@CurrentUser UserPrincipal userPrincipal, @RequestBody CreateTournamentRequest body) {
        User user = userService.getUserFromPrincipal(userPrincipal);
        Tournament tournament = tournamentService.createTournament(body, user);
        return ResponseEntity.ok(tournament);
    }

    @GetMapping("/k")
    public Tournament createFakePlayers() {
        PlayerBasedTournament tournament = (PlayerBasedTournament) tournamentService.getTournamentById(1L);
        User user = userService.getUserById(2L);
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setJoinedAt(LocalDateTime.now());
        participantRepository.save(participant);
        tournament.getPlayers().add(participant);
        return tournamentService.save(tournament);
    }
}
