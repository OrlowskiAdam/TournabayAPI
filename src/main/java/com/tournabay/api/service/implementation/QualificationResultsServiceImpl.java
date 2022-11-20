package com.tournabay.api.service.implementation;

import com.tournabay.api.dto.QualificationResultDto;
import com.tournabay.api.dto.TeamBasedQualificationResultDto;
import com.tournabay.api.dto.TeamScoresDto;
import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.IncorrectTournamentType;
import com.tournabay.api.model.*;
import com.tournabay.api.model.beatmap.Beatmap;
import com.tournabay.api.model.qualifications.QualificationRoom;
import com.tournabay.api.model.qualifications.TeamBasedQualificationRoom;
import com.tournabay.api.model.qualifications.results.ParticipantScore;
import com.tournabay.api.model.qualifications.results.PlayerBasedQualificationResult;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import com.tournabay.api.model.qualifications.results.TeamBasedQualificationResult;
import com.tournabay.api.osu.OsuApiClient;
import com.tournabay.api.osu.model.*;
import com.tournabay.api.payload.NewQualificationScore;
import com.tournabay.api.repository.ParticipantScoreRepository;
import com.tournabay.api.repository.QualificationResultRepository;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualificationResultsServiceImpl implements QualificationResultsService {
    private final MappoolService mappoolService;
    private final TeamService teamService;
    private final BeatmapService beatmapService;
    private final ParticipantService participantService;
    private final QualificationResultRepository qualificationResultRepository;
    private final ParticipantScoreRepository participantScoreRepository;

//    @PostConstruct
//    public void init() {
//        qualificationResultRepository.deleteAll();
//    }

    @Override
    public QualificationResult save(QualificationResult qualificationResult) {
        return qualificationResultRepository.save(qualificationResult);
    }

    @Override
    public List<QualificationResult> saveAll(Iterable<QualificationResult> qualificationResults) {
        return qualificationResultRepository.saveAll(qualificationResults);
    }

    @Override
    public MultiplayerLobbyData getDataFromOsuApi(Long lobbyId, User user) {
        try (OsuApiClient osuApiClient = new OsuApiClient(user.getOsuToken())) {
            return osuApiClient.getMatchData(lobbyId).orElseThrow(() -> new RuntimeException("Couldn't get score from Osu! API"));
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public List<QualificationResult> submitQualificationScores(MultiplayerLobbyData multiplayerLobbyData, QualificationRoom qualificationRoom, Tournament tournament) {
        // remove events where `game` is null
        List<MatchEvent> matchEvents = multiplayerLobbyData.getEvents()
                .stream()
                .filter(matchEvent -> matchEvent.getGame() != null)
                .collect(Collectors.toList());
        multiplayerLobbyData.setEvents(matchEvents);

        // set teams results data
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;

            // get involved teams
            Set<Team> teamsInLobby = determineTeamsInLobby(multiplayerLobbyData, teamBasedTournament);
            TeamBasedQualificationRoom teamBasedQualificationRoom;

            // check if all teams are in lobby
            if (qualificationRoom instanceof TeamBasedQualificationRoom) {
                teamBasedQualificationRoom = (TeamBasedQualificationRoom) qualificationRoom;
                boolean areTeamsInRoom = new HashSet<>(teamBasedQualificationRoom.getTeams()).containsAll(teamsInLobby);
                if (!areTeamsInRoom && teamsInLobby.size() != teamBasedQualificationRoom.getTeams().size()) {
                    throw new BadRequestException("One or more teams are not in this qualification room!");
                }
            } else {
                throw new IncorrectTournamentType("This tournament is not team based!");
            }

            Set<QualificationResult> results = new HashSet<>();

            // get flat list of participants from teamsInLobby variable
            List<Participant> participants = teamsInLobby.stream()
                    .flatMap(team -> team.getParticipants().stream())
                    .collect(Collectors.toList());
            for (Participant participant : participants) {
                // find existing result for participant
                // if not found, create new one
                // if found, update it
                TeamBasedQualificationResult teamBasedQualificationResult = teamBasedTournament.getQualificationResults()
                        .stream()
                        .map(qualificationResult -> (TeamBasedQualificationResult) qualificationResult)
                        .filter(result -> result.getParticipant().equals(participant))
                        .findFirst()
                        .orElse(TeamBasedQualificationResult.builder()
                                .participant(participant)
                                .participantScores(new ArrayList<>())
                                .team(participant.getTeam())
                                .qualificationRoom(teamBasedQualificationRoom)
                                .tournament(teamBasedTournament)
                                .build());
                results.add(teamBasedQualificationResult);
            }

            // find beatmaps in mappool
            List<Beatmap> beatmaps = mappoolService.findByStage(tournament, Stage.QUALIFIER)
                    .getBeatmapModifications()
                    .stream()
                    .flatMap(beatmapModification -> beatmapModification.getBeatmaps().stream())
                    .collect(Collectors.toList());

            // set scores
            for (MatchEvent event : multiplayerLobbyData.getEvents()) {
                for (MatchScores score : event.getGame().getScores()) {
                    Participant participant = participantService.getByOsuIdOrNull(score.getUser_id(), tournament);
                    // if participant is null, then it's probably a referee - skip
                    if (participant == null) continue;
                    Beatmap beatmap = beatmapService.findByBeatmapsetIdInList(event.getGame().getBeatmap().getBeatmapset_id(), beatmaps);
                    // skip beatmaps that were played but are not in the mappool
                    if (beatmap == null) continue;
                    TeamBasedQualificationResult teamBasedQualificationResult = results
                            .stream()
                            .map(result -> (TeamBasedQualificationResult) result)
                            .filter(result -> result.getParticipant().equals(participant))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Couldn't find participant in results list"));
                    ParticipantScore participantScore = teamBasedQualificationResult.getParticipantScores()
                            .stream()
                            .filter(participantScore1 -> participantScore1.getBeatmap().equals(beatmap))
                            .findFirst()
                            .orElse(null);
                    if (participantScore == null) {
                        participantScore = ParticipantScore
                                .builder()
                                .beatmap(beatmap)
                                .score(score.getScore())
                                .accuracy(Double.valueOf(score.getAccuracy()))
                                .result(teamBasedQualificationResult)
                                .build();
                    } else {
                        participantScore.setScore(score.getScore());
                        participantScore.setAccuracy(Double.valueOf(score.getAccuracy()));
                    }
                    teamBasedQualificationResult.getParticipantScores().add(participantScore);
                    results.add(teamBasedQualificationResult);
                }
            }
            return this.saveAll(results);
        } else if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
        }

        throw new IncorrectTournamentType("Incorrect tournament type!");
    }

    @Override
    public Set<Team> determineTeamsInLobby(MultiplayerLobbyData multiplayerLobbyData, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            Set<Team> teamsInLobby = new HashSet<>();
            for (OsuUser osuUser : multiplayerLobbyData.getUsers()) {
                Team team = teamService.getTeamByParticipantOsuIdWithoutThrow(osuUser.getId(), tournament);
                if (team != null) teamsInLobby.add(team);
            }
            return teamsInLobby;
        }
        throw new IncorrectTournamentType("Tournament is not team based");
    }

    @Override
    public List<QualificationResultDto> getTeamBasedQualificationResults(Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            List<TeamBasedQualificationResult> results = tournament.getQualificationResults()
                    .stream()
                    .map(result -> (TeamBasedQualificationResult) result)
                    .collect(Collectors.toList());
            List<QualificationResultDto> qualificationResultDtos = new ArrayList<>();

            // find beatmaps in mappool
            List<Beatmap> beatmaps = mappoolService.findByStage(tournament, Stage.QUALIFIER)
                    .getBeatmapModifications()
                    .stream()
                    .flatMap(beatmapModification -> beatmapModification.getBeatmaps().stream())
                    .collect(Collectors.toList());

            List<Team> teams = teamBasedTournament.getTeams();

            for (Team team : teams) {
                List<TeamBasedQualificationResult> teamResults = results
                        .stream()
                        .filter(result -> result.getTeam().equals(team))
                        .collect(Collectors.toList());
                if (teamResults.isEmpty()) continue;
                TeamBasedQualificationResultDto teamBasedQualificationResultDto = TeamBasedQualificationResultDto.builder()
                        .team(team)
                        .scores(new ArrayList<>())
                        .build();
                for (Beatmap beatmap : beatmaps) {
                    List<ParticipantScore> participantScores = teamResults
                            .stream()
                            .flatMap(result -> result.getParticipantScores().stream())
                            .filter(participantScore -> participantScore.getBeatmap().equals(beatmap))
                            .collect(Collectors.toList());
                    List<TeamScoresDto> teamScoresDtos = new ArrayList<>();
                    if (participantScores.size() > 0) {
                        TeamScoresDto teamScoresDto = TeamScoresDto.builder()
                                .beatmap(beatmap)
                                .averageScore(participantScores.stream().mapToDouble(ParticipantScore::getScore).average().orElse(0))
                                .averageAccuracy(participantScores.stream().mapToDouble(ParticipantScore::getAccuracy).average().orElse(0))
                                .build();
                        teamScoresDtos.add(teamScoresDto);
                    }
                    teamBasedQualificationResultDto.getScores().addAll(teamScoresDtos);
                }
                qualificationResultDtos.add(teamBasedQualificationResultDto);
            }
            calculateTeamBasedQualificationPoints(qualificationResultDtos, tournament);
            return qualificationResultDtos;
        } else if (tournament instanceof PlayerBasedTournament) {
            throw new NotYetImplementedException();
        }
        throw new NotYetImplementedException();
    }

    @Override
    public Double findHighestDivider(ScoreType scoreType, List<TeamScoresDto> beatmapScores) {
        if (scoreType.equals(ScoreType.ACCURACY)) {
            return beatmapScores.stream().mapToDouble(TeamScoresDto::getAverageAccuracy).max().orElse(0);
        } else {
            return beatmapScores.stream().mapToDouble(TeamScoresDto::getAverageScore).max().orElse(0);
        }
    }

    @Override
    public List<TeamScoresDto> sortTeamScores(List<TeamScoresDto> teamScores, ScoreType scoreType) {
        if (scoreType.equals(ScoreType.ACCURACY)) {
            return teamScores.stream().sorted(Comparator.comparingDouble(TeamScoresDto::getAverageAccuracy).reversed()).collect(Collectors.toList());
        } else {
            return teamScores.stream().sorted(Comparator.comparingDouble(TeamScoresDto::getAverageScore).reversed()).collect(Collectors.toList());
        }
    }

    @Override
    public List<QualificationResult> getQualificationResultsByTeam(Team team, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            return tournament.getQualificationResults().stream()
                    .map((result) -> (TeamBasedQualificationResult) result)
                    .filter(result -> result.getTeam().equals(team))
                    .collect(Collectors.toList());
        }
        throw new IncorrectTournamentType("Tournament is not team based");
    }

    @Transactional
    @Override
    public List<QualificationResultDto> updateQualificationResults(List<NewQualificationScore> newScores, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            List<TeamBasedQualificationResult> qualificationResults = tournament.getQualificationResults()
                    .stream()
                    .map(result -> (TeamBasedQualificationResult) result)
                    .collect(Collectors.toList());
            for (NewQualificationScore newScore : newScores) {
                ParticipantScore participantScore = qualificationResults
                        .stream()
                        .flatMap(result -> result.getParticipantScores().stream())
                        .filter(score -> score.getId().equals(newScore.getParticipantScoreId()))
                        .findFirst()
                        .orElseThrow(() -> new BadRequestException("Participant score not found"));
                participantScore.setScore(newScore.getNewScore());
                participantScore.setAccuracy(newScore.getNewAccuracy());
                participantScoreRepository.save(participantScore);
            }
            return this.getTeamBasedQualificationResults(tournament);
        } else if (tournament instanceof PlayerBasedTournament) {
            throw new NotYetImplementedException();
        }
        throw new IncorrectTournamentType("Incorrect tournament type!");
    }

    @Override
    public List<TeamBasedQualificationResultDto> calculateTeamBasedQualificationPoints(List<QualificationResultDto> qualificationResults, Tournament tournament) {
        // find beatmaps in mappool
        List<Beatmap> beatmaps = mappoolService.findByStage(tournament, Stage.QUALIFIER)
                .getBeatmapModifications()
                .stream()
                .flatMap(beatmapModification -> beatmapModification.getBeatmaps().stream())
                .collect(Collectors.toList());
        if (tournament instanceof TeamBasedTournament) {
            List<TeamBasedQualificationResultDto> teamBasedQualificationResultDtos = qualificationResults
                    .stream()
                    .map(result -> (TeamBasedQualificationResultDto) result)
                    .collect(Collectors.toList());
            for (Beatmap beatmap : beatmaps) {
                List<TeamScoresDto> beatmapScores = teamBasedQualificationResultDtos
                        .stream()
                        .flatMap(result -> result.getScores().stream())
                        .filter(teamScoresDto -> teamScoresDto.getBeatmap().equals(beatmap))
                        .collect(Collectors.toList());
                beatmapScores = sortTeamScores(beatmapScores, tournament.getScoreType());
                Double highestDivider = findHighestDivider(tournament.getScoreType(), beatmapScores);
                for (TeamScoresDto teamScoresDto : beatmapScores) {
                    if (tournament.getScoreType().equals(ScoreType.ACCURACY)) {
                        teamScoresDto.setQualificationPoints(teamScoresDto.getAverageAccuracy() / highestDivider);
                    } else {
                        teamScoresDto.setQualificationPoints(teamScoresDto.getAverageScore() / highestDivider);
                    }
                }
            }
            for (TeamBasedQualificationResultDto teamBasedQualificationResultDto : teamBasedQualificationResultDtos) {
                double totalQualificationPoints = teamBasedQualificationResultDto.getScores()
                        .stream()
                        .mapToDouble(TeamScoresDto::getQualificationPoints)
                        .sum();
                teamBasedQualificationResultDto.setQualificationPoints(totalQualificationPoints);
            }
            return teamBasedQualificationResultDtos;
        }
        throw new IncorrectTournamentType("Tournament is not a team based tournament");
    }

    @Transactional
    @Override
    public List<QualificationResultDto> deleteQualificationResultsByTeam(Team team, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            List<TeamBasedQualificationResult> qualificationResults = tournament.getQualificationResults()
                    .stream()
                    .map(result -> (TeamBasedQualificationResult) result)
                    .filter(result -> result.getTeam().equals(team))
                    .collect(Collectors.toList());
            for (TeamBasedQualificationResult qualificationResult : qualificationResults) {
                qualificationResult.setTournament(null);
                tournament.getQualificationResults().remove(qualificationResult);
            }
            qualificationResultRepository.deleteAll(qualificationResults);
            return this.getTeamBasedQualificationResults(tournament);
        } else if (tournament instanceof PlayerBasedTournament) {
            throw new NotYetImplementedException();
        }
        throw new IncorrectTournamentType("Incorrect tournament type!");
    }

    @Override
    public List<QualificationResultDto> deleteQualificationResultsByParticipant(Participant participant, Tournament tournament) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedQualificationResult teamBasedQualificationResult = tournament.getQualificationResults()
                    .stream()
                    .map(result -> (TeamBasedQualificationResult) result)
                    .filter(result -> result.getParticipant().equals(participant))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Qualification result not found!"));
            teamBasedQualificationResult.setTournament(null);
            tournament.getQualificationResults().remove(teamBasedQualificationResult);
            qualificationResultRepository.delete(teamBasedQualificationResult);
            return this.getTeamBasedQualificationResults(tournament);
        } else if (tournament instanceof PlayerBasedTournament) {
            throw new NotYetImplementedException();
        }
        throw new IncorrectTournamentType("Incorrect tournament type!");
    }

    public Set<Participant> determineParticipantsInLobby(MultiplayerLobbyData multiplayerLobbyData, Tournament tournament) {
        Set<Participant> participantsInLobby = new HashSet<>();
        for (OsuUser osuUser : multiplayerLobbyData.getUsers()) {
            Participant participant = participantService.getByOsuId(osuUser.getId(), tournament);
            if (participant != null) participantsInLobby.add(participant);
        }
        return participantsInLobby;
    }

}
