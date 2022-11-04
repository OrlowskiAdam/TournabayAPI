package com.tournabay.api.service.implementation;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.IncorrectMatchType;
import com.tournabay.api.exception.IncorrectTournamentType;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.*;
import com.tournabay.api.repository.MatchRepository;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final GroupService groupService;
    private final ParticipantService participantService;
    private final StaffMemberService staffMemberService;
    private final TeamService teamService;
    private final MatchResultService matchResultService;

    @Override
    public Match save(Match match) {
        return matchRepository.save(match);
    }

    @Override
    public Match findById(Tournament tournament, Long id) {
        if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            return teamBasedTournament.getMatches()
                    .stream()
                    .filter(match -> match.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Match not found!"));
        } else if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            return playerBasedTournament.getMatches()
                    .stream()
                    .filter(match -> match.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Match not found!"));
        }
        throw new BadRequestException("Tournament type not supported!");
    }

    @Override
    @Transactional
    public Match createPlayerVsMatch(Tournament tournament, CreatePlayerVsMatchRequest createMatchRequest) {
        if (createMatchRequest.getRedParticipantId().equals(createMatchRequest.getBlueParticipantId()))
            throw new BadRequestException("Red and blue participant cannot be the same!");
        if (tournament instanceof TeamBasedTournament) throw new IncorrectTournamentType("Incorrect tournament type!");
        PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
        Participant redParticipant = participantService.getById(createMatchRequest.getRedParticipantId(), playerBasedTournament);
        Participant blueParticipant = participantService.getById(createMatchRequest.getBlueParticipantId(), playerBasedTournament);
        ParticipantVsMatch match = ParticipantVsMatch.builder()
                .startDate(createMatchRequest.getStartDate().toLocalDate())
                .startTime(createMatchRequest.getStartDate().toLocalTime())
                .ldt(createMatchRequest.getStartDate())
                .isCompleted(false)
                .isLive(createMatchRequest.getIsLive())
                .refereesLimit(1)
                .commentatorsLimit(2)
                .streamersLimit(1)
                .redParticipant(redParticipant)
                .blueParticipant(blueParticipant)
                .stage(createMatchRequest.getStage())
                .tournament(playerBasedTournament)
                .build();
        match = (ParticipantVsMatch) setStaffMembers(playerBasedTournament, match, createMatchRequest);
        if (createMatchRequest.getStage().equals(Stage.GROUP_STAGE)) {
            if (createMatchRequest.getGroupId() == null)
                throw new BadRequestException("Group id is required for group stage!");
            Group group = groupService.findById(playerBasedTournament, createMatchRequest.getGroupId());
            groupService.assignMatchToGroup(playerBasedTournament, group, match);
        }
        return match;
    }

    @Override
    @Transactional
    public Match createTeamVsMatch(Tournament tournament, CreateTeamVsMatchRequest createMatchRequest) {
        if (createMatchRequest.getRedTeamId().equals(createMatchRequest.getBlueTeamId()))
            throw new BadRequestException("Red team and blue team can't be the same!");
        if (tournament instanceof PlayerBasedTournament)
            throw new IncorrectTournamentType("Incorrect tournament type!");
        TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
        Team redTeam = teamService.getById(createMatchRequest.getRedTeamId(), teamBasedTournament);
        Team blueTeam = teamService.getById(createMatchRequest.getBlueTeamId(), teamBasedTournament);
        TeamVsMatch match = TeamVsMatch.builder()
                .startDate(createMatchRequest.getStartDate().toLocalDate())
                .startTime(createMatchRequest.getStartDate().toLocalTime())
                .ldt(createMatchRequest.getStartDate())
                .isCompleted(false)
                .isLive(createMatchRequest.getIsLive())
                .refereesLimit(1)
                .commentatorsLimit(2)
                .streamersLimit(1)
                .redTeam(redTeam)
                .blueTeam(blueTeam)
                .stage(createMatchRequest.getStage())
                .tournament(teamBasedTournament)
                .build();
        match = (TeamVsMatch) setStaffMembers(teamBasedTournament, match, createMatchRequest);
        if (createMatchRequest.getStage().equals(Stage.GROUP_STAGE)) {
            if (createMatchRequest.getGroupId() == null)
                throw new BadRequestException("Group id is required for group stage!");
            Group group = groupService.findById(teamBasedTournament, createMatchRequest.getGroupId());
            groupService.assignMatchToGroup(teamBasedTournament, group, match);
        }
        return match;
    }

    @Override
    public Match setStaffMembers(Tournament tournament, Match match, CreateMatchRequest createMatchRequest) {
        List<StaffMember> referees = staffMemberService.getStaffMembersById(createMatchRequest.getRefereesId(), tournament);
        List<StaffMember> commentators = staffMemberService.getStaffMembersById(createMatchRequest.getCommentatorsId(), tournament);
        List<StaffMember> streamers = staffMemberService.getStaffMembersById(createMatchRequest.getStreamersId(), tournament);
        match.setReferees(new HashSet<>(referees));
        match.setCommentators(new HashSet<>(commentators));
        match.setStreamers(new HashSet<>(streamers));
        return this.save(match);
    }

    @Override
    public Match setStaffMembers(Tournament tournament, Match match, UpdateMatchRequest updateMatchRequest) {
        List<StaffMember> referees = staffMemberService.getStaffMembersById(updateMatchRequest.getRefereesId(), tournament);
        match.setReferees(new HashSet<>(referees));
        if (updateMatchRequest.getIsLive()) {
            List<StaffMember> commentators = staffMemberService.getStaffMembersById(updateMatchRequest.getCommentatorsId(), tournament);
            List<StaffMember> streamers = staffMemberService.getStaffMembersById(updateMatchRequest.getStreamersId(), tournament);
            match.setCommentators(new HashSet<>(commentators));
            match.setStreamers(new HashSet<>(streamers));
        } else {
            match.setCommentators(new HashSet<>());
            match.setStreamers(new HashSet<>());
        }
        return this.save(match);
    }

    @Override
    @Transactional
    public Match removeMatchById(Tournament tournament, Long matchId) {
        Match match = this.findById(tournament, matchId);
        if (match.getIsCompleted()) throw new BadRequestException("Cannot remove completed match!");
        if (match.getStage().equals(Stage.GROUP_STAGE)) {
            Group group = groupService.getGroupByMatchId(tournament, match.getId());
            groupService.excludeMatchFromGroup(tournament, group, match);
        }
//        if (match instanceof TeamVsMatch) {
//            TeamVsMatch teamVsMatch = (TeamVsMatch) match;
//            teamVsMatch.setTournament(null);
//        } else if (match instanceof ParticipantVsMatch) {
//            ParticipantVsMatch participantVsMatch = (ParticipantVsMatch) match;
//            participantVsMatch.setTournament(null);
//        }
        matchRepository.delete(match);
        return match;
    }

    @Override
    @Transactional
    public Match updatePlayerVsMatch(Tournament tournament, Match match, UpdatePlayerVsMatch updateMatchRequest) {
        if (match instanceof TeamVsMatch) throw new IncorrectMatchType("Incorrect match type!");
        ParticipantVsMatch participantVsMatch = (ParticipantVsMatch) match;
        Participant redParticipant = participantService.getById(updateMatchRequest.getRedParticipantId(), tournament);
        Participant blueParticipant = participantService.getById(updateMatchRequest.getBlueParticipantId(), tournament);
        if (match.getStage().equals(Stage.GROUP_STAGE)) {
            Group group = groupService.getGroupByMatchId(tournament, match.getId());
            boolean areParticipantsInGroup = groupService.areParticipantsInGroup(group, redParticipant, blueParticipant);
            if (!areParticipantsInGroup)
                throw new BadRequestException("One or both participants are not in the group " + group.getSymbol() + "!");
        }
        participantVsMatch.setRedParticipant(redParticipant);
        participantVsMatch.setBlueParticipant(blueParticipant);
        participantVsMatch.setIsLive(updateMatchRequest.getIsLive());
        participantVsMatch.setStartDate(updateMatchRequest.getStartDate().toLocalDate());
        participantVsMatch.setStartTime(updateMatchRequest.getStartDate().toLocalTime());
        participantVsMatch.setLdt(updateMatchRequest.getStartDate());
        participantVsMatch.setRefereesLimit(updateMatchRequest.getRefereesLimit());
        participantVsMatch.setCommentatorsLimit(updateMatchRequest.getCommentatorsLimit());
        participantVsMatch.setStreamersLimit(updateMatchRequest.getStreamersLimit());
        participantVsMatch = (ParticipantVsMatch) this.setStaffMembers(tournament, participantVsMatch, updateMatchRequest);
        return this.save(participantVsMatch);
    }

    @Override
    @Transactional
    public Match updateTeamVsMatch(Tournament tournament, Match match, UpdateTeamVsMatch updateMatchRequest) {
        if (match instanceof ParticipantVsMatch) throw new IncorrectMatchType("Incorrect match type!");
        TeamVsMatch teamVsMatch = (TeamVsMatch) match;
        Team redTeam = teamService.getById(updateMatchRequest.getRedTeamId(), tournament);
        Team blueTeam = teamService.getById(updateMatchRequest.getBlueTeamId(), tournament);
        if (match.getStage().equals(Stage.GROUP_STAGE)) {
            Group group = groupService.getGroupByMatchId(tournament, match.getId());
            boolean areTeamsInGroup = groupService.areTeamsInGroup(group, redTeam, blueTeam);
            if (!areTeamsInGroup)
                throw new BadRequestException("One or both teams are not in the group " + group.getSymbol() + "!");
        }
        teamVsMatch.setRedTeam(redTeam);
        teamVsMatch.setBlueTeam(blueTeam);
        teamVsMatch.setIsLive(updateMatchRequest.getIsLive());
        teamVsMatch.setStartDate(updateMatchRequest.getStartDate().toLocalDate());
        teamVsMatch.setStartTime(updateMatchRequest.getStartDate().toLocalTime());
        teamVsMatch.setLdt(updateMatchRequest.getStartDate());
        teamVsMatch.setRefereesLimit(updateMatchRequest.getRefereesLimit());
        teamVsMatch.setCommentatorsLimit(updateMatchRequest.getCommentatorsLimit());
        teamVsMatch.setStreamersLimit(updateMatchRequest.getStreamersLimit());
        teamVsMatch = (TeamVsMatch) this.setStaffMembers(tournament, teamVsMatch, updateMatchRequest);
        return this.save(teamVsMatch);
    }

    @Override
    @Transactional
    public Match submitResult(Tournament tournament, Match match, MatchResultRequest matchResultRequest) {
        if (match.getIsCompleted()) throw new BadRequestException("Match is already completed!");
        if (tournament instanceof TeamBasedTournament && match instanceof TeamVsMatch) {
            TeamVsMatch teamVsMatch = (TeamVsMatch) match;
            MatchResult matchResult = matchResultService.createMatchResult(matchResultRequest);
            teamVsMatch.setMatchResult(matchResult);
            teamVsMatch.setIsCompleted(true);
            Team winner = matchResultService.determineTeamWinner(teamVsMatch, matchResult);
            Team loser = matchResultService.determineTeamLoser(teamVsMatch, matchResult);
            teamVsMatch.setWinner(winner);
            teamVsMatch.setLoser(loser);
            return this.save(teamVsMatch);
        } else if (tournament instanceof PlayerBasedTournament && match instanceof ParticipantVsMatch) {
            ParticipantVsMatch participantVsMatch = (ParticipantVsMatch) match;
            MatchResult matchResult = matchResultService.createMatchResult(matchResultRequest);
            participantVsMatch.setMatchResult(matchResult);
            participantVsMatch.setIsCompleted(true);
            Participant winner = matchResultService.determineParticipantWinner(participantVsMatch, matchResult);
            Participant loser = matchResultService.determineParticipantLoser(participantVsMatch, matchResult);
            participantVsMatch.setWinner(winner);
            participantVsMatch.setLoser(loser);
            return this.save(participantVsMatch);
        }
        throw new BadRequestException("Incorrect match type!");
    }
}
