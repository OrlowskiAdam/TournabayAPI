package com.tournabay.api.service;

import com.tournabay.api.dto.QualificationResultDto;
import com.tournabay.api.dto.TeamBasedQualificationResultDto;
import com.tournabay.api.dto.TeamScoresDto;
import com.tournabay.api.model.*;
import com.tournabay.api.model.qualifications.QualificationRoom;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import com.tournabay.api.osu.model.MultiplayerLobbyData;
import com.tournabay.api.payload.NewQualificationScore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface QualificationResultsService {

    QualificationResult save(QualificationResult qualificationResult);

    List<QualificationResult> saveAll(Iterable<QualificationResult> qualificationResults);

    MultiplayerLobbyData getDataFromOsuApi(Long lobbyId, User user);

    List<QualificationResult> submitQualificationScores(MultiplayerLobbyData multiplayerLobbyData, QualificationRoom qualificationRoom, Tournament tournament);

    Set<Team> determineTeamsInLobby(MultiplayerLobbyData multiplayerLobbyData, Tournament tournament);

    List<QualificationResultDto> getTeamBasedQualificationResults(Tournament tournament);

    Double findHighestDivider(ScoreType scoreType, List<TeamScoresDto> beatmapScores);

    List<TeamScoresDto> sortTeamScores(List<TeamScoresDto> teamScores, ScoreType scoreType);

    List<QualificationResult> getQualificationResultsByTeam(Team team, Tournament tournament);

    List<QualificationResultDto> updateQualificationResults(List<NewQualificationScore> newScores, Tournament tournament);

    List<TeamBasedQualificationResultDto> calculateTeamBasedQualificationPoints(List<QualificationResultDto> qualificationResults, Tournament tournament);

    List<QualificationResultDto> deleteQualificationResultsByTeam(Team team, Tournament tournament);

    List<QualificationResultDto> deleteQualificationResultsByParticipant(Participant participant, Tournament tournament);
}
