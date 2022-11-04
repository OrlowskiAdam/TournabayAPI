package com.tournabay.api.service.implementation;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.IncorrectMatchType;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.MatchResultRequest;
import com.tournabay.api.repository.MatchResultRepository;
import com.tournabay.api.service.MatchResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchResultServiceImpl implements MatchResultService {
    private final MatchResultRepository matchResultRepository;

    @Override
    public MatchResult save(MatchResult matchResult) {
        return matchResultRepository.save(matchResult);
    }

    @Override
    public MatchResult createMatchResult(MatchResultRequest matchResultRequest) {
        MatchResult matchResult = MatchResult.builder()
                .blueScore(matchResultRequest.getBlueScore())
                .redScore(matchResultRequest.getRedScore())
                .lobbyLink(matchResultRequest.getLobbyLink())
                .build();
        return this.save(matchResult);
    }

    @Override
    public Participant determineParticipantWinner(Match match, MatchResult matchResult) {
        if (match instanceof ParticipantVsMatch) {
            ParticipantVsMatch participantVsMatch = (ParticipantVsMatch) match;
            if (matchResult.getRedScore() > matchResult.getBlueScore()) {
                return participantVsMatch.getRedParticipant();
            } else {
                return participantVsMatch.getBlueParticipant();
            }
        }
        throw new IncorrectMatchType("Match is not a ParticipantVsMatch");
    }

    @Override
    public Participant determineParticipantLoser(Match match, MatchResult matchResult) {
        if (match instanceof ParticipantVsMatch) {
            ParticipantVsMatch participantVsMatch = (ParticipantVsMatch) match;
            if (matchResult.getRedScore() < matchResult.getBlueScore()) {
                return participantVsMatch.getRedParticipant();
            } else {
                return participantVsMatch.getBlueParticipant();
            }
        }
        throw new IncorrectMatchType("Match is not a ParticipantVsMatch");
    }

    @Override
    public Team determineTeamWinner(Match match, MatchResult matchResult) {
        if (match instanceof TeamVsMatch) {
            TeamVsMatch teamVsMatch = (TeamVsMatch) match;
            if (matchResult.getRedScore().equals(matchResult.getBlueScore()))
                throw new BadRequestException("Match cannot be a draw!");
            else if (matchResult.getRedScore() > matchResult.getBlueScore()) {
                return teamVsMatch.getRedTeam();
            } else {
                return teamVsMatch.getBlueTeam();
            }
        }
        throw new IncorrectMatchType("Match is not a TeamVsMatch");
    }

    @Override
    public Team determineTeamLoser(Match match, MatchResult matchResult) {
        if (match instanceof TeamVsMatch) {
            TeamVsMatch teamVsMatch = (TeamVsMatch) match;
            if (matchResult.getRedScore().equals(matchResult.getBlueScore()))
                throw new BadRequestException("Match cannot be a draw!");
            else if (matchResult.getRedScore() < matchResult.getBlueScore()) {
                return teamVsMatch.getRedTeam();
            } else {
                return teamVsMatch.getBlueTeam();
            }
        }
        throw new IncorrectMatchType("Match is not a TeamVsMatch");
    }
}
