package com.tournabay.api.service;

import com.tournabay.api.model.ScoreType;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.User;
import com.tournabay.api.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    public Tournament createTournament(String name, ScoreType scoreType, LocalDateTime startDate, LocalDateTime endDate, User owner) {
        Tournament tournament = new Tournament(name, startDate, endDate, scoreType, owner);
        return tournamentRepository.save(tournament);
    }
}
