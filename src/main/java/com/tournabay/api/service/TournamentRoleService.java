package com.tournabay.api.service;

import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.repository.TournamentRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentRoleService {
    private final TournamentRoleRepository tournamentRoleRepository;

    public List<TournamentRole> createDefaultTournamentRoles(Tournament tournament) {
        List<TournamentRole> tournamentRoles = new ArrayList<>();
        tournamentRoles.add(new TournamentRole("Host", tournament, true));
        tournamentRoles.add(new TournamentRole("Organizer", tournament, false));
        tournamentRoles.add(new TournamentRole("Pooler", tournament, false));
        tournamentRoles.add(new TournamentRole("Referee", tournament, false));
        tournamentRoles.add(new TournamentRole("Commentator", tournament, false));
        tournamentRoles.add(new TournamentRole("Streamer", tournament, false));
        tournamentRoles.add(new TournamentRole("Uncategorized", tournament, true));
        return tournamentRoleRepository.saveAll(tournamentRoles);
    }
}
