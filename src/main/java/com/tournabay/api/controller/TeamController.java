package com.tournabay.api.controller;

import com.tournabay.api.model.Team;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {
    private final TournamentService tournamentService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final TeamService teamService;
    private final ParticipantService participantService;

    @PostMapping("/create/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Team> createTeam(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, Team team) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageTeams(),
                tournament.getPermission().getCanStaffMemberManageTeams()
        );

        return null;
    }
}
