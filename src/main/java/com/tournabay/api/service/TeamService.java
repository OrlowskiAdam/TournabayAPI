package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final ParticipantService participantService;

    /**
     * Update the team to the database.
     *
     * @param team The team object that is being saved.
     * @return The team object that was saved.
     */
    public Team update(Team team, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            Settings teamBasedTournamentSettings = teamBasedTournament.getSettings();
            Integer baseTeamSize = teamBasedTournamentSettings.getBaseTeamSize();
            Integer maxTeamSize = teamBasedTournamentSettings.getMaxTeamSize();
            if (team.getParticipants().size() < baseTeamSize || team.getParticipants().size() > maxTeamSize) {
                throw new BadRequestException("Team size must be between " + baseTeamSize + " and " + maxTeamSize);
            }
            return teamRepository.save(team);
        }
        throw new BadRequestException("Invalid tournament type!");
    }


    /**
     * Creates a team for a team based tournament
     *
     * @param name The name of the team
     * @param participantIds A list of participant IDs that will be added to the team.
     * @param seed The seed of the team.
     * @param teamStatus The status of the team.
     * @param tournament The tournament that the team is being created for.
     * @return A Team object
     */
    public Team createTeam(String name, List<Long> participantIds, Seed seed, TeamStatus teamStatus, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            // Check if the team name is unique
            if (checkForUniqueTeamName(name, teamBasedTournament))
                throw new BadRequestException("Team name already exists!");
            // Check if the team size is valid
            Settings settings = teamBasedTournament.getSettings();
            Integer maxTeamSize = settings.getMaxTeamSize();
            if (participantIds.size() > maxTeamSize)
                throw new BadRequestException("Team size cannot be greater than " + maxTeamSize);
            // Check if the participants are not in other teams
            List<Participant> participants = participantService.getAllByIds(participantIds, tournament);
            if (checkForDuplicatedParticipants(participants, teamBasedTournament))
                throw new BadRequestException("One or more participant is already in other team!");
            // First participant in the list is the captain
            Participant captain = participants.size() > 0 ? participants.get(0) : null;
            Team team = Team.builder()
                    .name(name)
                    .seed(seed)
                    .status(teamStatus)
                    .captain(captain)
                    .participants(new HashSet<>(participants))
                    .tournament(teamBasedTournament)
                    .build();
            Team newTeam = teamRepository.save(team);
            participants.forEach(participant -> participant.setTeam(newTeam));
            participantService.saveAll(participants);
            return newTeam;
        }
        throw new BadRequestException("Invalid tournament type!");
    }

    /**
     * If the team exists, return it, otherwise throw an exception.
     *
     * @param id The id of the team to be found.
     * @return A team object
     */
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team not found!"));
    }

    /**
     * Check if any of the participants in the list of participants are already in the tournament's team
     *
     * @param participants The list of participants that are being added to the team.
     * @param tournament The tournament to check for duplicate participants in.
     * @return A boolean value.
     */
    private boolean checkForDuplicatedParticipants(List<Participant> participants, TeamBasedTournament tournament) {
        return tournament.getTeams()
                .stream()
                .anyMatch(team -> team.getParticipants()
                        .stream()
                        .anyMatch(participant -> participants
                                .stream()
                                .anyMatch(participant1 -> participant1.getId().equals(participant.getId())
                                )));
    }

    /**
     * Check if any of the teams in the tournament have the same name as the one passed in.
     *
     * @param name The name of the team to be created
     * @param tournament The tournament that the team is being added to.
     * @return A boolean value.
     */
    private boolean checkForUniqueTeamName(String name, TeamBasedTournament tournament) {
        return tournament.getTeams()
                .stream()
                .anyMatch(team -> team.getName().equals(name));
    }
}
