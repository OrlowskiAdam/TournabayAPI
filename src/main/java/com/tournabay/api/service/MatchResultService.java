package com.tournabay.api.service;

import com.tournabay.api.model.Match;
import com.tournabay.api.model.MatchResult;
import com.tournabay.api.model.Participant;
import com.tournabay.api.model.Team;
import com.tournabay.api.payload.MatchResultRequest;
import org.springframework.stereotype.Service;

@Service
public interface MatchResultService {
    MatchResult save(MatchResult matchResult);

    MatchResult createMatchResult(MatchResultRequest matchResultRequest);

    Participant determineParticipantWinner(Match match, MatchResult matchResult);

    Participant determineParticipantLoser (Match match, MatchResult matchResult);

    Team determineTeamWinner(Match match, MatchResult matchResult);

    Team determineTeamLoser (Match match, MatchResult matchResult);
}
