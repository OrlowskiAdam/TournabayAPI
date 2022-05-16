package com.tournabay.api.controller;

import com.tournabay.api.model.Team;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.payload.CreateTeamRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {
    private final TournamentService tournamentService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final TeamService teamService;

    /**
     * Create a team for a tournament, if the user has permission to do so.
     *
     * The first thing we do is get the tournament from the database. Then we get the user from the user principal. Then we
     * check if the user has permission to create a team. Then we create the team
     *
     * @param userPrincipal The user who is making the request.
     * @param tournamentId The id of the tournament the team is being created for
     * @param createTeamRequest This is the request body that is sent to the server. It contains the name of the team, the
     * participant ids, the seed, the status, and the tournament.
     * @return A ResponseEntity with a body of type Team.
     */
    @PostMapping("/create/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Team> createTeam(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @Valid @RequestBody CreateTeamRequest createTeamRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        User user = userService.getUserFromPrincipal(userPrincipal);
        permissionService.hasAccess(
                tournament,
                user,
                tournament.getPermission().getCanTournamentRoleManageTeams(),
                tournament.getPermission().getCanStaffMemberManageTeams()
        );
        Team team = teamService.createTeam(
                createTeamRequest.getName(),
                createTeamRequest.getParticipantIds(),
                createTeamRequest.getSeed(),
                createTeamRequest.getStatus(),
                tournament
        );
        return ResponseEntity.ok().body(team);
    }
}
