package com.tournabay.api.controller;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.model.Page;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.payload.CreateTournamentRoleRequest;
import com.tournabay.api.repository.TournamentRepository;
import com.tournabay.api.repository.TournamentRoleRepository;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.TournamentRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/tournament-role")
public class TournamentRoleController {
    private final TournamentRepository tournamentRepository;
    private final TournamentRoleService tournamentRoleService;
    private final TournamentRoleRepository tournamentRoleRepository;

    @PostMapping("/add/{tournamentId}")
    @Secured("ROLE_USER")
    // TODO: Security check
    public ResponseEntity<TournamentRole> addTournamentRole(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody CreateTournamentRoleRequest createTournamentRoleRequest) {
        if (createTournamentRoleRequest.getRoleName() == null || createTournamentRoleRequest.getRoleName().equals("")) throw new BadRequestException("Please specify role name!");
        Tournament tournament = tournamentRepository.getById(tournamentId);
        TournamentRole tournamentRole;
        if (createTournamentRoleRequest.getInherit() == null || createTournamentRoleRequest.getInherit().equals("")) {
            tournamentRole = tournamentRoleService.createRole(createTournamentRoleRequest.getRoleName(), tournament);
        } else {
            tournamentRole = tournamentRoleService.createRole(createTournamentRoleRequest.getRoleName(), createTournamentRoleRequest.getInherit(), tournament);
        }
        return ResponseEntity.status(201).body(tournamentRole);
    }
}
