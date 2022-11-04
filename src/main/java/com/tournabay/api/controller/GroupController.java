package com.tournabay.api.controller;

import com.tournabay.api.model.Group;
import com.tournabay.api.model.Match;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.GroupService;
import com.tournabay.api.service.MatchService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;
    private final TournamentService tournamentService;
    private final MatchService matchService;

    /**
     * Return a group by its id and tournament id
     *
     * @param userPrincipal The userPrincipal is the user that is currently logged in.
     * @param groupId       The id of the group you want to get.
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @return A group object
     */
    @GetMapping("/{groupId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> getGroupById(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        return ResponseEntity.ok(group);
    }

    /**
     * Return a list of groups for a given tournament
     *
     * @param userPrincipal The userPrincipal object is used to get the userId of the user who is currently logged in.
     * @param tournamentId  The id of the tournament that the groups belong to.
     * @return A list of groups
     */
    @GetMapping("/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<List<Group>> getGroupsByTournamentId(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<Group> groups = groupService.findAll(tournament);
        return ResponseEntity.ok(groups);
    }

    /**
     * Get all matches in a group
     *
     * @param userPrincipal The user who is currently logged in.
     * @param groupId       the id of the group
     * @param tournamentId  The id of the tournament that the group is in.
     * @return A list of matches in a group
     */
    @GetMapping("/{groupId}/tournament/{tournamentId}/matches")
    @Secured("ROLE_USER")
    public ResponseEntity<List<Match>> getMatchesInGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        List<Match> matches = groupService.getMatchesInGroup(group);
        return ResponseEntity.ok(matches);
    }

    /**
     * Create a group for a tournament
     *
     * @param userPrincipal The userPrincipal object is created by the JWT filter and contains the user's information.
     * @param tournamentId  The id of the tournament that the group is being created for.
     * @return A group object
     */
    @PostMapping("/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> createGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.createGroup(tournament);
        return ResponseEntity.ok(group);
    }

    /**
     * Delete a group from a tournament
     *
     * @param userPrincipal The user who is currently logged in.
     * @param groupId       The id of the group you want to delete.
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @return A group object
     */
    @DeleteMapping("/{groupId}/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<List<Group>> deleteGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        List<Group> groups = groupService.deleteGroup(tournament, group);
        return ResponseEntity.ok(groups);
    }

    /**
     * Assign a team to a group
     *
     * @param userPrincipal The userPrincipal is the user that is currently logged in.
     * @param groupId       The id of the group you want to assign the team to.
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @param teamId        The id of the team to be assigned to the group
     * @return A group object with the team assigned to it.
     */
    @PostMapping("/{groupId}/tournament/{tournamentId}/team/{teamId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> assignTeamToGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId, @PathVariable Long teamId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        group = groupService.assignTeamToGroup(tournament, group, teamId);
        return ResponseEntity.ok(group);
    }

    /**
     * Assign a participant to a group
     *
     * @param userPrincipal The userPrincipal is the user that is currently logged in.
     * @param groupId       The id of the group you want to assign the participant to.
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @param participantId The id of the participant to be assigned to the group
     * @return A group object
     */
    @PostMapping("/{groupId}/tournament/{tournamentId}/participant/{participantId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> assignParticipantToGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId, @PathVariable Long participantId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        group = groupService.assignParticipantToGroup(tournament, group, participantId);
        return ResponseEntity.ok(group);
    }

    /**
     * It removes a team from a group.
     *
     * @param userPrincipal The user that is currently logged in.
     * @param groupId       The id of the group you want to remove the team from
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @param teamId        The id of the team to be removed from the group
     * @return A group object
     */
    @DeleteMapping("/{groupId}/tournament/{tournamentId}/team/{teamId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> removeTeamFromGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId, @PathVariable Long teamId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        group = groupService.removeTeamFromGroup(tournament, group, teamId);
        return ResponseEntity.ok(group);
    }

    /**
     * Removes a participant from a group
     *
     * @param userPrincipal The user who is currently logged in.
     * @param groupId       The id of the group you want to remove the participant from
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @param participantId The id of the participant to be removed from the group
     * @return A group object
     */
    @DeleteMapping("/{groupId}/tournament/{tournamentId}/participant/{participantId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> removeParticipantFromGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId, @PathVariable Long participantId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Group group = groupService.findById(tournament, groupId);
        group = groupService.removeParticipantFromGroup(tournament, group, participantId);
        return ResponseEntity.ok(group);
    }

    /**
     * Assign a match to a group
     *
     * @param userPrincipal The user who is currently logged in.
     * @param groupId       The id of the group to which the match is to be assigned
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @param matchId       The id of the match to be assigned to the group
     * @return A group object with the match assigned to it.
     */
    @PostMapping("/{groupId}/tournament/{tournamentId}/match/{matchId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> assignMatchToGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId, @PathVariable Long matchId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.findById(tournament, matchId);
        Group group = groupService.findById(tournament, groupId);
        group = groupService.assignMatchToGroup(tournament, group, match);
        return ResponseEntity.ok(group);
    }

    /**
     * Exclude a match from a group
     *
     * @param userPrincipal The user who is currently logged in.
     * @param groupId       the id of the group you want to exclude the match from
     * @param tournamentId  The id of the tournament that the group belongs to.
     * @param matchId       the id of the match to be excluded from the group
     * @return A group object
     */
    @PostMapping("/{groupId}/tournament/{tournamentId}/match/{matchId}/exclude")
    @Secured("ROLE_USER")
    public ResponseEntity<Group> excludeMatchFromGroup(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long groupId, @PathVariable Long tournamentId, @PathVariable Long matchId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Match match = matchService.findById(tournament, matchId);
        Group group = groupService.findById(tournament, groupId);
        group = groupService.excludeMatchFromGroup(tournament, group, match);
        return ResponseEntity.ok(group);
    }

}
