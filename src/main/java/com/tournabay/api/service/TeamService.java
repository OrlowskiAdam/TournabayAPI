package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.Team;
import com.tournabay.api.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team not found!"));
    }
}
