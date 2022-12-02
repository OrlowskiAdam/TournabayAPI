package com.tournabay.api.controller;

import com.tournabay.api.model.Team;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.CreateTeamRequest;
import com.tournabay.api.service.TeamService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {
    private final TournamentService tournamentService;
    private final TeamService teamService;

    /**
     * Create a team for a tournament, if the user has permission to do so.
     * <p>
     * The first thing we do is get the tournament from the database. Then we get the user from the user principal. Then we
     * check if the user has permission to create a team. Then we create the team
     *
     * @param tournamentId      The id of the tournament the team is being created for
     * @param createTeamRequest This is the request body that is sent to the server. It contains the name of the team, the
     *                          participant ids, the seed, the status, and the tournament.
     * @return A ResponseEntity with a body of type Team.
     */
    @PostMapping("/create/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Teams')")
    public ResponseEntity<Team> createTeam(@PathVariable Long tournamentId, @Valid @RequestBody CreateTeamRequest createTeamRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Team team = teamService.createTeam(
                createTeamRequest.getName(),
                createTeamRequest.getCaptainId(),
                createTeamRequest.getParticipantIds(),
                createTeamRequest.getSeed(),
                createTeamRequest.getStatus(),
                tournament
        );
        return ResponseEntity.ok().body(team);
    }

    /**
     * "Delete a team from a tournament, but only if the user has permission to manage teams in that tournament."
     *
     * @param teamId       The id of the team to be deleted
     * @param tournamentId The id of the tournament that the team belongs to.
     * @return A team object
     */
    @DeleteMapping("/delete/{teamId}/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Teams')")
    public ResponseEntity<Team> deleteTeam(@PathVariable Long teamId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Team team = teamService.getById(teamId, tournament);
        teamService.removeTeam(tournament, team);
        return ResponseEntity.ok().body(team);
    }

    /**
     * This function updates a team in a tournament
     *
     * @param teamId            The id of the team to update
     * @param tournamentId      The id of the tournament that the team is in.
     * @param createTeamRequest This is the request body that is sent to the API. It contains the following fields:
     * @return A ResponseEntity with the updated team.
     */
    @PutMapping("/update/{teamId}/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Teams')")
    public ResponseEntity<Team> updateTeam(@PathVariable Long teamId, @PathVariable Long tournamentId, @Valid @RequestBody CreateTeamRequest createTeamRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Team team = teamService.findById(teamId);
        Team updatedTeam = teamService.updateTeam(
                tournament,
                team,
                createTeamRequest.getName(),
                createTeamRequest.getCaptainId(),
                createTeamRequest.getParticipantIds(),
                createTeamRequest.getSeed(),
                createTeamRequest.getStatus()
        );
        return ResponseEntity.ok().body(updatedTeam);
    }
}
