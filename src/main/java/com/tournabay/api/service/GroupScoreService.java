package com.tournabay.api.service;

import com.tournabay.api.model.PlayerGroupScore;
import com.tournabay.api.model.TeamGroupScore;
import org.springframework.stereotype.Service;

@Service
public interface GroupScoreService {

    TeamGroupScore save(TeamGroupScore teamGroupScore);

    PlayerGroupScore save(PlayerGroupScore playerGroupScore);
}
