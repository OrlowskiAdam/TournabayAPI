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

    /**
     * Save the team to the database.
     *
     * @param team The team object that is being saved.
     * @return The team object that was saved.
     */
    public Team save(Team team) {
        return teamRepository.save(team);
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
}
