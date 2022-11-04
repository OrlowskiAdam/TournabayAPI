package com.tournabay.api.service.implementation;

import com.tournabay.api.model.PlayerGroupScore;
import com.tournabay.api.model.TeamGroupScore;
import com.tournabay.api.repository.PlayerGroupScoreRepository;
import com.tournabay.api.repository.TeamGroupScoreRepository;
import com.tournabay.api.service.GroupScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupScoreServiceImpl implements GroupScoreService {
    private final PlayerGroupScoreRepository playerGroupScoreRepository;
    private final TeamGroupScoreRepository teamGroupScoreRepository;

    @Override
    public TeamGroupScore save(TeamGroupScore teamGroupScore) {
        return teamGroupScoreRepository.save(teamGroupScore);
    }

    @Override
    public PlayerGroupScore save(PlayerGroupScore playerGroupScore) {
        return playerGroupScoreRepository.save(playerGroupScore);
    }
}
