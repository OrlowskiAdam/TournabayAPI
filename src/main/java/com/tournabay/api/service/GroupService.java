package com.tournabay.api.service;

import com.tournabay.api.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {

    /**
     * Find a group by its id in the given tournament.
     *
     * @param tournament The tournament that the group belongs to.
     * @param id         The id of the group to find.
     * @return A Group object
     */
    Group findById(Tournament tournament, Long id);

    /**
     * Find all groups for a given tournament.
     *
     * @param tournament The tournament to find groups for.
     * @return A list of all the groups in the tournament.
     */
    List<Group> findAll(Tournament tournament);

    /**
     * Get all the matches in a group.
     *
     * @param group The group you want to get the matches from.
     * @return A list of matches in a group.
     */
    List<Match> getMatchesInGroup(Group group);

    /**
     * Create a group for the given tournament.
     *
     * @param tournament The tournament object that will be related with the group.
     * @return A Group object.
     */
    Group createGroup(Tournament tournament);

    /**
     * Delete a group from a tournament.
     *
     * @param tournament The tournament object that the group is in.
     * @param group      The group to be deleted.
     * @return A list of groups with newly assigned symbols.
     */
    List<Group> deleteGroup(Tournament tournament, Group group);

    /**
     * Assign a team to a group
     *
     * @param tournament The tournament that the group belongs to.
     * @param group      The group to assign the team to.
     * @param teamId     The id of the team to assign to the group.
     * @return A TeamBasedGroup object
     */
    TeamBasedGroup assignTeamToGroup(Tournament tournament, Group group, Long teamId);

    /**
     * Assign a participant to a group
     *
     * @param tournament    The tournament that the group belongs to.
     * @param group         The group to assign the participant to.
     * @param participantId The id of the participant to assign to the group.
     * @return A PlayerBasedGroup object.
     */
    PlayerBasedGroup assignParticipantToGroup(Tournament tournament, Group group, Long participantId);

    /**
     * Remove a team from a group
     *
     * @param tournament The tournament that the group belongs to.
     * @param group      The group to remove the team from
     * @param teamId     The id of the team to be removed from the group.
     * @return A TeamBasedGroup
     */
    TeamBasedGroup removeTeamFromGroup(Tournament tournament, Group group, Long teamId);

    /**
     * Remove a participant from a group
     *
     * @param tournament    The tournament that the group belongs to.
     * @param group         The group to remove the participant from.
     * @param participantId The id of the participant to remove from the group.
     * @return A PlayerBasedGroup
     */
    PlayerBasedGroup removeParticipantFromGroup(Tournament tournament, Group group, Long participantId);

    /**
     * Assign a match to a group in a tournament.
     *
     * @param tournament The tournament that the group belongs to.
     * @param group      The group to assign the match to.
     * @param match      The match to be assigned to the group.
     * @return A Group object.
     */
    Group assignMatchToGroup(Tournament tournament, Group group, Match match);

    /**
     * Exclude a match from a group in a tournament.
     *
     * @param tournament The tournament object that the group belongs to.
     * @param group      The group to exclude the match from.
     * @param match      The match to exclude from the group.
     * @return A Group
     */
    Group excludeMatchFromGroup(Tournament tournament, Group group, Match match);

    /**
     * Given a list of groups, return a new list of groups where each group has a unique symbol.
     *
     * @param groups A list of groups.
     * @return A list of groups.
     */
    List<Group> reassignSymbols(List<Group> groups);

    /**
     * Get new symbol for another group;
     *
     * @param tournament The tournament object containing all groups.
     * @return A Group object with a symbol assigned to it.
     */
    Character getNewSymbol(Tournament tournament);

    /**
     * Detach all participants/teams from the group
     *
     * @param group The group to detach items from.
     * @return The group that was passed in.
     */
    Group detachItemsFromGroup(Group group);

}
