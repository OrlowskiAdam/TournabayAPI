package com.tournabay.api.service.implementation;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.IncorrectGroupType;
import com.tournabay.api.exception.IncorrectTournamentType;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.GroupRepository;
import com.tournabay.api.service.GroupService;
import com.tournabay.api.service.ParticipantService;
import com.tournabay.api.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
        List<Group> groups = tournament.getGroups()
                .stream()
                .filter(g -> !g.getId().equals(group.getId()))
                .collect(Collectors.toList());
        group.setTournament(null);
        detachItemsFromGroup(group);
        groupRepository.delete(group);
        groups = reassignSymbols(groups);
        tournament.setGroups(groups);
        return groups;
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
                teamBasedGroup.getTeams().add(team);
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
                        .anyMatch(g -> ((PlayerBasedGroup) g).getParticipants().stream().anyMatch(p -> p.getId().equals(participantId)));
                if (isParticipantInAnyGroup) throw new BadRequestException("Participant is already in a group");
                Participant participant = participantService.getById(participantId, playerBasedTournament);
                playerBasedGroup.getParticipants().add(participant);
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
                        .anyMatch(team -> team.getId().equals(teamId));
                if (!isTeamInGroup) throw new BadRequestException("Team is not in the group");
                Team team = teamService.getById(teamId, teamBasedTournament);
                teamBasedGroup.getTeams().removeIf(t -> t.getId().equals(team.getId()));
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
                        .anyMatch(participant -> participant.getId().equals(participantId));
                if (!isParticipantInGroup) throw new BadRequestException("Participant is not in the group");
                Participant participant = participantService.getById(participantId, playerBasedTournament);
                playerBasedGroup.getParticipants()
                        .removeIf(p -> p.getId().equals(participant.getId()));
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
    public Group assignMatchToGroup(Tournament tournament, Group group, Match match) {
        group.getMatches().add(match);
        return groupRepository.save(group);
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
