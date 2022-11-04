package com.tournabay.api.service.implementation;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.IncorrectGroupType;
import com.tournabay.api.exception.IncorrectTournamentType;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.GroupRepository;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final TeamService teamService;
    private final ParticipantService participantService;
    private final GroupScoreService groupScoreService;
    private final TournamentService tournamentService;

    /**
     * It saves the group to the database.
     *
     * @param group The group object that is being saved.
     * @return The group object is being returned.
     */
    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    /**
     * Find the group with the given id in the given tournament, or throw a ResourceNotFoundException if it doesn't exist.
     *
     * @param tournament The tournament that the group belongs to.
     * @param id         The id of the group to be found
     * @return A Group object
     */
    @Override
    public Group findById(Tournament tournament, Long id) {
        return tournament
                .getGroups()
                .stream()
                .filter(group -> group.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    /**
     * Return all the groups in the tournament.
     *
     * @param tournament The tournament to find the groups for.
     * @return A list of groups
     */
    @Override
    public List<Group> findAll(Tournament tournament) {
        return tournament.getGroups();
    }

    /**
     * Return the list of matches in the given group.
     *
     * @param group The group to get the matches from.
     * @return A list of matches in a group.
     */
    @Override
    public List<Match> getMatchesInGroup(Group group) {
        return group.getMatches();
    }

    /**
     * Find the group that contains the match with the given id.
     *
     * @param tournament The tournament object that contains the groups
     * @param matchId    The id of the match we want to find the group for.
     * @return A group that contains a match with the given matchId.
     */
    @Override
    public Group getGroupByMatchId(Tournament tournament, Long matchId) {
        return tournament.getGroups()
                .stream()
                .filter(group -> group.getMatches()
                        .stream()
                        .anyMatch(match -> match.getId().equals(matchId)))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    /**
     * It creates a new group for a tournament, and adds it to the tournament's list of groups
     *
     * @param tournament The tournament to which the group will be added.
     * @return A Group object
     */
    @Override
    public Group createGroup(Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            Character symbol = getNewSymbol(tournament);
            TeamBasedGroup group = TeamBasedGroup.builder()
                    .teams(new ArrayList<>())
                    .tournament(teamBasedTournament)
                    .matches(new ArrayList<>())
                    .symbol(symbol)
                    .build();
            teamBasedTournament.getGroups().add(group);
            return groupRepository.save(group);
        } else if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            Character symbol = getNewSymbol(tournament);
            PlayerBasedGroup group = PlayerBasedGroup.builder()
                    .participants(new ArrayList<>())
                    .tournament(playerBasedTournament)
                    .matches(new ArrayList<>())
                    .symbol(symbol)
                    .build();
            playerBasedTournament.getGroups().add(group);
            return groupRepository.save(group);
        }
        throw new IncorrectTournamentType("Incorrect tournament type");
    }

    /**
     * Delete a group from a tournament, reassign symbols to the remaining groups, and return the deleted group.
     *
     * @param tournament The tournament that the group is in.
     * @param group      The group to be deleted
     * @return The group that was deleted.
     */
    @Override
    public List<Group> deleteGroup(Tournament tournament, Group group) {
//        List<Group> groups = tournament.getGroups()
//                .stream()
//                .filter(g -> !g.getId().equals(group.getId()))
//                .collect(Collectors.toList());
//        group.setTournament(null);
//        detachItemsFromGroup(group);
//        groupRepository.delete(group);
//        groups = reassignSymbols(groups);
//        tournament.setGroups(groups);
//        return groups;
        tournament.getGroups().remove(group);
        group.setTournament(null);
        tournamentService.save(tournament);
        return tournament.getGroups();
    }

    /**
     * If the tournament is team based, and the group is team based, then add the team to the group.
     *
     * @param tournament The tournament to which the group belongs.
     * @param group      The group to which the team or participant is to be assigned.
     * @param teamId     The id of the team to be assigned to the group
     * @return TeamBasedGroup
     */
    @Override
    public TeamBasedGroup assignTeamToGroup(Tournament tournament, Group group, Long teamId) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            if (group instanceof TeamBasedGroup) {
                TeamBasedGroup teamBasedGroup = (TeamBasedGroup) group;
                Team team = teamService.getById(teamId, teamBasedTournament);
                boolean isTeamInAnyGroup = teamBasedTournament.getGroups()
                        .stream()
                        .map(g -> (TeamBasedGroup) g)
                        .anyMatch(g -> g.getTeams().stream().anyMatch(t -> t.getTeam().getId().equals(team.getId())));
                if (isTeamInAnyGroup) throw new BadRequestException("Team is already in a group");
                TeamGroupScore teamGroupScore = TeamGroupScore.builder()
                        .team(team)
                        .wins(0)
                        .losses(0)
                        .build();
                teamGroupScore = groupScoreService.save(teamGroupScore);
                teamBasedGroup.getTeams().add(teamGroupScore);
                return groupRepository.save(teamBasedGroup);
            }
            throw new IncorrectGroupType("Incorrect group type! Should be team based.");
        }
        throw new IncorrectTournamentType("Incorrect tournament type! Should be team based.");
    }

    /**
     * If the tournament is player based and the group is player based, add the participant to the group and save the
     * group.
     *
     * @param tournament    The tournament that the group belongs to.
     * @param group         The group to which the participant will be assigned.
     * @param participantId The id of the participant to be assigned to the group.
     * @return A PlayerBasedGroup
     */
    @Override
    public PlayerBasedGroup assignParticipantToGroup(Tournament tournament, Group group, Long participantId) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            if (group instanceof PlayerBasedGroup) {
                PlayerBasedGroup playerBasedGroup = (PlayerBasedGroup) group;
                boolean isParticipantInAnyGroup = playerBasedTournament
                        .getGroups()
                        .stream()
                        .anyMatch(g -> ((PlayerBasedGroup) g).getParticipants().stream().anyMatch(p -> p.getParticipant().getId().equals(participantId)));
                if (isParticipantInAnyGroup) throw new BadRequestException("Participant is already in a group");
                Participant participant = participantService.getById(participantId, playerBasedTournament);
                PlayerGroupScore playerGroupScore = PlayerGroupScore.builder()
                        .participant(participant)
                        .wins(0)
                        .losses(0)
                        .build();
                playerGroupScore = groupScoreService.save(playerGroupScore);
                playerBasedGroup.getParticipants().add(playerGroupScore);
                return groupRepository.save(playerBasedGroup);
            }
            throw new IncorrectGroupType("Incorrect group type! Should be participant based.");
        }
        throw new IncorrectTournamentType("Incorrect tournament type! Should be participant based.");
    }

    /**
     * If the tournament is team based, and the group is team based, and the team is in the group, then remove the team
     * from the group.
     *
     * @param tournament The tournament that the group belongs to.
     * @param group      The group to remove the team from
     * @param teamId     The id of the team to be removed from the group
     * @return TeamBasedGroup
     */
    @Override
    public TeamBasedGroup removeTeamFromGroup(Tournament tournament, Group group, Long teamId) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            if (group instanceof TeamBasedGroup) {
                TeamBasedGroup teamBasedGroup = (TeamBasedGroup) group;
                boolean isTeamInGroup = teamBasedGroup
                        .getTeams()
                        .stream()
                        .anyMatch(team -> team.getTeam().getId().equals(teamId));
                if (!isTeamInGroup) throw new BadRequestException("Team is not in the group");
                boolean isGroupMatchAssociatedWithTeam = teamBasedGroup
                        .getMatches()
                        .stream()
                        .filter(match -> match instanceof TeamVsMatch)
                        .filter(match -> match.getStage().equals(Stage.GROUP_STAGE))
                        .anyMatch(match -> ((TeamVsMatch) match).getRedTeam().getId().equals(teamId) || ((TeamVsMatch) match).getBlueTeam().getId().equals(teamId));
                if (isGroupMatchAssociatedWithTeam)
                    throw new BadRequestException("Team is associated with a match! Remove matches first before removing team from group.");
                Team team = teamService.getById(teamId, teamBasedTournament);
                teamBasedGroup.getTeams().removeIf(t -> t.getTeam().getId().equals(team.getId()));
                return groupRepository.save(teamBasedGroup);
            }
            throw new IncorrectGroupType("Incorrect group type! Should be team based.");
        }
        throw new IncorrectTournamentType("Incorrect tournament type! Should be team based.");
    }

    /**
     * Remove a participant from a group
     *
     * @param tournament    The tournament that the group belongs to.
     * @param group         The group to remove the participant from.
     * @param participantId The id of the participant to be removed from the group.
     * @return A PlayerBasedGroup
     */
    @Override
    public PlayerBasedGroup removeParticipantFromGroup(Tournament tournament, Group group, Long participantId) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            if (group instanceof PlayerBasedGroup) {
                PlayerBasedGroup playerBasedGroup = (PlayerBasedGroup) group;
                boolean isParticipantInGroup = playerBasedGroup
                        .getParticipants()
                        .stream()
                        .anyMatch(participant -> participant.getParticipant().getId().equals(participantId));
                if (!isParticipantInGroup) throw new BadRequestException("Participant is not in the group");
                boolean isParticipantAssociatedWithMatch = playerBasedGroup
                        .getMatches()
                        .stream()
                        .filter(match -> match instanceof ParticipantVsMatch)
                        .filter(match -> match.getStage().equals(Stage.GROUP_STAGE))
                        .anyMatch(match -> ((ParticipantVsMatch) match).getRedParticipant().getId().equals(participantId) || ((ParticipantVsMatch) match).getBlueParticipant().getId().equals(participantId));
                if (isParticipantAssociatedWithMatch)
                    throw new BadRequestException("Participant is associated with a match! Remove matches first before removing participant from group.");
                Participant participant = participantService.getById(participantId, playerBasedTournament);
                playerBasedGroup.getParticipants()
                        .removeIf(p -> p.getParticipant().getId().equals(participant.getId()));
                return groupRepository.save(playerBasedGroup);
            }
            throw new IncorrectGroupType("Incorrect group type! Should be participant based.");
        }
        throw new IncorrectTournamentType("Incorrect tournament type! Should be participant based.");
    }

    /**
     * Add a match to a group and save the group.
     *
     * @param tournament The tournament that the group belongs to.
     * @param group      The group to which the match is to be assigned.
     * @param match      The match to be assigned to the group
     * @return A Group object
     */
    @Override
    @Transactional
    public Group assignMatchToGroup(Tournament tournament, Group group, Match match) {
        if (tournament instanceof TeamBasedTournament && group instanceof TeamBasedGroup && match instanceof TeamVsMatch) {
            boolean areTeamsInGroup = this.areTeamsInGroup(group, ((TeamVsMatch) match).getRedTeam(), ((TeamVsMatch) match).getBlueTeam());
            if (!areTeamsInGroup) throw new BadRequestException("One or both teams are not in the group!");
            group.getMatches().add(match);
            return groupRepository.save(group);
        } else if (tournament instanceof PlayerBasedTournament && group instanceof PlayerBasedGroup && match instanceof ParticipantVsMatch) {
            boolean areParticipantsInGroup = this.areParticipantsInGroup(group, ((ParticipantVsMatch) match).getRedParticipant(), ((ParticipantVsMatch) match).getBlueParticipant());
            if (!areParticipantsInGroup)
                throw new BadRequestException("One or both participants are not in the group!");
            group.getMatches().add(match);
            return groupRepository.save(group);
        }
        throw new BadRequestException("Incorrect match type for the group type");
    }

    /**
     * If the group is a PlayerBasedGroup, return true if either participant is in the group, otherwise return false.
     *
     * @param group        The group to check if the participants are in.
     * @param participant1 The first participant to check
     * @param participant2 The second participant to check.
     * @return Boolean
     */
    @Override
    public Boolean areParticipantsInGroup(Group group, Participant participant1, Participant participant2) {
        if (group instanceof PlayerBasedGroup) {
            PlayerBasedGroup playerBasedGroup = (PlayerBasedGroup) group;
            return playerBasedGroup.getParticipants()
                    .stream()
                    .anyMatch(p -> p.getParticipant().equals(participant1) || p.getParticipant().equals(participant2));
        }
        return false;
    }

    /**
     * If the group is a TeamBasedGroup, then return true if either team1 or team2 is in the group.
     *
     * @param group The group to check if the teams are in.
     * @param team1 The first team to check
     * @param team2 The team that is being checked to see if it is in the group.
     * @return Boolean
     */
    @Override
    public Boolean areTeamsInGroup(Group group, Team team1, Team team2) {
        if (group instanceof TeamBasedGroup) {
            TeamBasedGroup teamBasedGroup = (TeamBasedGroup) group;
            return teamBasedGroup.getTeams()
                    .stream()
                    .anyMatch(t -> t.getTeam().equals(team1))
                    &&
                    teamBasedGroup.getTeams()
                            .stream()
                            .anyMatch(t -> t.getTeam().equals(team2));
        }
        return false;
    }

    /**
     * If the group is a PlayerBasedGroup, return true if the participant is in the group, otherwise return false.
     *
     * @param group       The group to check if the participant is in.
     * @param participant The participant that you want to check if they are in the group.
     * @return Boolean
     */
    @Override
    public Boolean isParticipantInGroup(Group group, Participant participant) {
        if (group instanceof PlayerBasedGroup) {
            PlayerBasedGroup playerBasedGroup = (PlayerBasedGroup) group;
            return playerBasedGroup.getParticipants()
                    .stream()
                    .anyMatch(p -> p.getParticipant().equals(participant));
        }
        return false;
    }

    /**
     * If the group is a team based group, then return true if the team is in the group, otherwise return false.
     *
     * @param group The group to check if the team is in.
     * @param team  The team to check if it's in the group
     * @return A boolean value
     */
    @Override
    public Boolean isTeamInGroup(Group group, Team team) {
        if (group instanceof TeamBasedGroup) {
            TeamBasedGroup teamBasedGroup = (TeamBasedGroup) group;
            return teamBasedGroup.getTeams()
                    .stream()
                    .anyMatch(t -> t.getTeam().equals(team));
        }
        return false;
    }

    /**
     * Remove the match from the group and save the group.
     *
     * @param tournament The tournament that the group belongs to.
     * @param group      The group to which the match will be added.
     * @param match      The match to be excluded from the group
     * @return Group
     */
    @Override
    public Group excludeMatchFromGroup(Tournament tournament, Group group, Match match) {
        group.getMatches().remove(match);
        return groupRepository.save(group);
    }

    /**
     * Reassigns symbols to groups, starting with A, and saves the groups.
     *
     * @param groups The list of groups to reassign symbols to.
     * @return A list of groups
     */
    public List<Group> reassignSymbols(List<Group> groups) {
        Character symbol = 'A';
        for (Group group : groups) {
            group.setSymbol(symbol);
            symbol++;
        }
        return groupRepository.saveAll(groups);
    }

    /**
     * Get the next available group symbol by incrementing the last group symbol in the tournament.
     *
     * @param tournament The tournament that the group is being added to.
     * @return A character
     */
    @Override
    public Character getNewSymbol(Tournament tournament) {
        Character groupSymbol = 'A';
        List<Group> groups = tournament.getGroups();
        for (Group group : groups) {
            if (group.getSymbol().equals(groupSymbol)) {
                groupSymbol++;
            }
        }
        return groupSymbol;
    }

    /**
     * If the group is a PlayerBasedGroup, then set the participants to an empty list and save the group. If the group is
     * a TeamBasedGroup, then set the teams to an empty list and save the group. Otherwise, throw an IncorrectGroupType
     * exception.
     *
     * @param group The group to detach items from.
     * @return A group object.
     */
    @Override
    public Group detachItemsFromGroup(Group group) {
        if (group instanceof PlayerBasedGroup) {
            PlayerBasedGroup playerBasedGroup = (PlayerBasedGroup) group;
            playerBasedGroup.setParticipants(new ArrayList<>());
            return groupRepository.save(playerBasedGroup);
        } else if (group instanceof TeamBasedGroup) {
            TeamBasedGroup teamBasedGroup = (TeamBasedGroup) group;
            teamBasedGroup.setTeams(new ArrayList<>());
            return groupRepository.save(teamBasedGroup);
        }
        throw new IncorrectGroupType("Incorrect group type! Should be team based or participant based.");
    }

}
