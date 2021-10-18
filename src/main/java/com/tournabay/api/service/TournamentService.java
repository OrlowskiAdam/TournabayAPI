package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.CreateTournamentRequest;
import com.tournabay.api.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final ParticipantService participantService;
    private final TournamentRoleService tournamentRoleService;
    private final PageService pageService;

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tournament not found!"));
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Participant addParticipant(Tournament tournament, Participant participant) {
        if (!tournament.containsParticipant(participant)) {
            participant.setTournament(tournament);
            return participantService.save(participant);
        }
        throw new BadRequestException(participant.getUser().getUsername() + " is already a participant!");
    }

    // TODO: Code clean-up
    @Transactional
    public Tournament createTournament(CreateTournamentRequest body, User owner) {
        if (body.getTeamFormat().equals(TeamFormat.TEAM_VS)) {
            TeamBasedTournament tournament = TeamBasedTournament
                    .builder()
                    .name(body.getName())
                    .gameMode(body.getGameMode())
                    .scoreType(body.getScoreType())
                    .teamFormat(body.getTeamFormat())
                    .startDate(body.getStartDate())
                    .endDate(body.getEndDate())
                    .owner(owner)
                    .build();
            Tournament newTournament = tournamentRepository.save(tournament);
            List<TournamentRole> defaultTournamentRoles = tournamentRoleService.createDefaultTournamentRoles(newTournament);
            pageService.createTournamentPages(defaultTournamentRoles, newTournament);
            return newTournament;
        } else if (body.getTeamFormat().equals(TeamFormat.PLAYER_VS)) {
            PlayerBasedTournament tournament = PlayerBasedTournament
                    .builder()
                    .name(body.getName())
                    .gameMode(body.getGameMode())
                    .scoreType(body.getScoreType())
                    .teamFormat(body.getTeamFormat())
                    .startDate(body.getStartDate())
                    .endDate(body.getEndDate())
                    .owner(owner)
                    .build();
            Tournament newTournament = tournamentRepository.save(tournament);
            List<TournamentRole> defaultTournamentRoles = tournamentRoleService.createDefaultTournamentRoles(newTournament);
            pageService.createTournamentPages(defaultTournamentRoles, newTournament);
            return newTournament;
        }

        throw new BadRequestException("Unsupported team format");
    }
}
