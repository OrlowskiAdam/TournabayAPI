package com.tournabay.api.service;

import com.tournabay.api.model.Match;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.*;
import org.springframework.stereotype.Service;

@Service
public interface MatchService {

    Match save(Match match);

    Match findById(Tournament tournament, Long id);

    Match createPlayerVsMatch(Tournament tournament, CreatePlayerVsMatchRequest createMatchRequest);

    Match createTeamVsMatch(Tournament tournament, CreateTeamVsMatchRequest createMatchRequest);

    Match setStaffMembers(Tournament tournament, Match match, CreateMatchRequest createMatchRequest);

    Match setStaffMembers(Tournament tournament, Match match, UpdateMatchRequest updateMatchRequest);

    Match removeMatchById(Tournament tournament, Long matchId);

    Match updatePlayerVsMatch(Tournament tournament, Match match, UpdatePlayerVsMatch updateMatchRequest);

    Match updateTeamVsMatch(Tournament tournament, Match match, UpdateTeamVsMatch updateMatchRequest);

    Match submitResult(Tournament tournament, Match match, MatchResultRequest matchResultRequest);
}
