package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.CreateTournamentRequest;
import com.tournabay.api.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tournament not found!"));
    }

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
            return tournamentRepository.save(tournament);
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
            return tournamentRepository.save(tournament);
        }

        throw new BadRequestException("Unsupported team format");
    }
}
