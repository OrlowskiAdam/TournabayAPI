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

    public Team create(String name, Seed seed, TeamStatus teamStatus, List<Long> participantIds, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            Settings settings = teamBasedTournament.getSettings();
            Integer maxTeamSize = settings.getMaxTeamSize();
            if (participantIds.size() > maxTeamSize) {
                throw new BadRequestException("Team size must be between cannot be greater than " + maxTeamSize);
            }
            List<Participant> participants = participantService.getAllByIds(participantIds, tournament);
            Team team = Team.builder()
                    .name(name)
                    .seed(seed)
                    .status(teamStatus)
                    .participants(new HashSet<>(participants))
                    .build();
            return teamRepository.save(team);
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

    private boolean checkForDuplicateParticipants(List<Participant> participants, TeamBasedTournament tournament) {
        return tournament.getTeams()
                .stream()
                .anyMatch(team -> team.getParticipants()
                        .stream()
                        .anyMatch(participant -> participants
                                .stream()
                                .anyMatch(participant1 -> participant1.getId().equals(participant.getId())
                                )));
    }
}
