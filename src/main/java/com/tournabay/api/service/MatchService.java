package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.CreateMatchRequest;
import com.tournabay.api.payload.UpdateMatchRequest;
import com.tournabay.api.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final ParticipantService participantService;
    private final StaffMemberService staffMemberService;
    private final TeamService teamService;

    /**
     * If the tournament is a PlayerBasedTournament, return the match with the given id from the list of matches in the
     * tournament, otherwise if the tournament is a TeamBasedTournament, return the match with the given id from the list
     * of matches in the tournament, otherwise throw an exception.
     *
     * @param matchId    The id of the match to be retrieved.
     * @param tournament The tournament that the match belongs to.
     * @return Match
     */
    public Match getById(Long matchId, Tournament tournament) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            return playerBasedTournament.getMatches()
                    .stream()
                    .filter(match -> match.getId().equals(matchId))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Match not found in the tournament!"));
        } else if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            return teamBasedTournament.getMatches()
                    .stream()
                    .filter(match -> match.getId().equals(matchId))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Match not found in the tournament!"));
        }
        throw new BadRequestException("Unsupported tournament type!");
    }

    /**
     * Find the match with the given id in the tournament's matches and delete it
     *
     * @param matchId    The id of the match to be deleted.
     * @param tournament The tournament that the match belongs to.
     * @return Match
     */
    public Match deleteMatchById(Long matchId, Tournament tournament) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            Match match = playerBasedTournament.getMatches()
                    .stream()
                    .filter(m -> m.getId().equals(matchId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Match not found in the tournament!"));
            matchRepository.delete(match);
            return match;
        } else if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            Match match = teamBasedTournament.getMatches()
                    .stream()
                    .filter(m -> m.getId().equals(matchId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Match not found in the tournament!"));
            matchRepository.delete(match);
            return match;
        }
        throw new BadRequestException("Tournament type not supported!");
    }

    /**
     * If the tournament is a player based tournament, create a match with the player based tournament, otherwise create a
     * match with the team based tournament.
     *
     * @param tournament The tournament that the match is being created for.
     * @param body       The request body.
     * @return A Match object
     */
    public Match createMatch(Tournament tournament, CreateMatchRequest body) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            return createMatch(playerBasedTournament,
                    body.getRedParticipantId(),
                    body.getBlueParticipantId(),
                    body.getStartDate().toLocalDate(),
                    body.getStartDate().toLocalTime(),
                    body.getRefereesId(),
                    body.getCommentatorsId(),
                    body.getStreamersId(),
                    body.getIsLive()
            );
        } else if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            return createMatch(teamBasedTournament,
                    body.getRedTeamId(),
                    body.getBlueTeamId(),
                    body.getStartDate().toLocalDate(),
                    body.getStartDate().toLocalTime(),
                    body.getRefereesId(),
                    body.getCommentatorsId(),
                    body.getStreamersId(),
                    body.getIsLive()
            );
        }
        throw new BadRequestException("Invalid tournament type!");
    }

    /**
     * Update a match with the given data.
     *
     * It's just a bunch of checks and assignments
     *
     * @param tournament The tournament that the match belongs to.
     * @param match      The match to be updated
     * @param body       The request body.
     * @return Match
     */
    public Match updateMatch(Tournament tournament, Match match, UpdateMatchRequest body) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            ParticipantVsMatch participantVsMatch = (ParticipantVsMatch) match;
            return updateMatch(
                    participantVsMatch,
                    playerBasedTournament,
                    body.getRedParticipantId(),
                    body.getBlueParticipantId(),
                    body.getStartDate().toLocalDate(),
                    body.getStartDate().toLocalTime(),
                    body.getRefereesId(),
                    body.getCommentatorsId(),
                    body.getStreamersId(),
                    body.getIsLive(),
                    body.getRefereesLimit(),
                    body.getCommentatorsLimit(),
                    body.getStreamersLimit()
            );
        } else if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            TeamVsMatch teamVsMatch = (TeamVsMatch) match;
            return updateMatch(
                    teamVsMatch,
                    teamBasedTournament,
                    body.getRedTeamId(),
                    body.getBlueTeamId(),
                    body.getStartDate().toLocalDate(),
                    body.getStartDate().toLocalTime(),
                    body.getRefereesId(),
                    body.getCommentatorsId(),
                    body.getStreamersId(),
                    body.getIsLive(),
                    body.getRefereesLimit(),
                    body.getCommentatorsLimit(),
                    body.getStreamersLimit()
            );
        }
        throw new BadRequestException("Unsupported tournament type!");
    }

    /**
     * Create a match between two participants, with a list of referees, commentators, and streamers, and a start date and
     * time, and whether it's live.
     *
     * @param tournament        The tournament that the match is being created for.
     * @param redParticipantId  The id of the participant who will be the red participant in the match.
     * @param blueParticipantId The id of the participant that will be on the blue side of the match.
     * @param date              The date of the match
     * @param time              The time of the match
     * @param refereeIds        List of referee ids
     * @param commentatorIds    List of commentator ids
     * @param streamerIds       List of streamer ids
     * @param isLive            If the match is live or not.
     * @return A Match object
     */
    protected Match createMatch(PlayerBasedTournament tournament,
                                Long redParticipantId,
                                Long blueParticipantId,
                                LocalDate date,
                                LocalTime time,
                                List<Long> refereeIds,
                                List<Long> commentatorIds,
                                List<Long> streamerIds,
                                Boolean isLive
    ) {
        Participant redParticipant = participantService.getById(redParticipantId, tournament);
        Participant blueParticipant = participantService.getById(blueParticipantId, tournament);
        if (redParticipant.equals(blueParticipant)) {
            throw new BadRequestException("Red and blue participants cannot be the same!");
        }
        List<StaffMember> referees = staffMemberService.getStaffMembersById(refereeIds, tournament);
        List<StaffMember> commentators = new ArrayList<>();
        List<StaffMember> streamers = new ArrayList<>();
        if (isLive) {
            commentators = staffMemberService.getStaffMembersById(commentatorIds, tournament);
            streamers = staffMemberService.getStaffMembersById(streamerIds, tournament);
        }
        ParticipantVsMatch participantVsMatch = ParticipantVsMatch.builder()
                .redParticipant(redParticipant)
                .blueParticipant(blueParticipant)
                .referees(new HashSet<>(referees))
                .commentators(new HashSet<>(commentators))
                .streamers(new HashSet<>(streamers))
                .startDate(date)
                .startTime(time)
                .isLive(isLive)
                .isCompleted(false)
                .refereesLimit(tournament.getSettings().getRefereesLimit())
                .commentatorsLimit(tournament.getSettings().getCommentatorsLimit())
                .streamersLimit(tournament.getSettings().getStreamersLimit())
                .tournament(tournament)
                .build();
        return matchRepository.save(participantVsMatch);
    }

    /**
     * Create a match between two teams, with a list of referees, commentators, and streamers, and a start date and time,
     * and a boolean indicating whether the match is live or not.
     *
     * @param tournament     The tournament that the match is being created for.
     * @param redTeamId      The id of the red team
     * @param blueTeamId     The id of the blue team
     * @param date           The date of the match
     * @param time           The time of the match
     * @param refereeIds     List of referee ids
     * @param commentatorIds List of commentator ids
     * @param streamerIds    The list of streamers who will be streaming the match.
     * @param isLive         If the match is live or not
     * @return A Match object
     */
    protected Match createMatch(TeamBasedTournament tournament,
                                Long redTeamId,
                                Long blueTeamId,
                                LocalDate date,
                                LocalTime time,
                                List<Long> refereeIds,
                                List<Long> commentatorIds,
                                List<Long> streamerIds,
                                Boolean isLive
    ) {
        Team redTeam = teamService.getById(redTeamId, tournament);
        Team blueTeam = teamService.getById(blueTeamId, tournament);
        if (redTeam.equals(blueTeam)) {
            throw new BadRequestException("Red and blue participants cannot be the same!");
        }
        List<StaffMember> referees = staffMemberService.getStaffMembersById(refereeIds, tournament);
        List<StaffMember> commentators = new ArrayList<>();
        List<StaffMember> streamers = new ArrayList<>();
        if (isLive) {
            commentators = staffMemberService.getStaffMembersById(commentatorIds, tournament);
            streamers = staffMemberService.getStaffMembersById(streamerIds, tournament);
        }
        TeamVsMatch teamVsMatch = TeamVsMatch.builder()
                .redTeam(redTeam)
                .blueTeam(blueTeam)
                .referees(new HashSet<>(referees))
                .commentators(new HashSet<>(commentators))
                .streamers(new HashSet<>(streamers))
                .startDate(date)
                .startTime(time)
                .isLive(isLive)
                .isCompleted(false)
                .refereesLimit(tournament.getSettings().getRefereesLimit())
                .commentatorsLimit(tournament.getSettings().getCommentatorsLimit())
                .streamersLimit(tournament.getSettings().getStreamersLimit())
                .tournament(tournament)
                .build();
        return matchRepository.save(teamVsMatch);
    }

    /**
     * It updates a match with the given parameters
     *
     * @param match             the match to be updated
     * @param tournament        the tournament in which the match is being played
     * @param redParticipantId  The id of the participant that will be on the red side.
     * @param blueParticipantId The id of the blue participant
     * @param date              The date of the match
     * @param time              LocalTime
     * @param refereeIds        List of referee ids
     * @param commentatorIds    List of commentator ids
     * @param streamerIds       List of streamer ids
     * @param isLive            if the match is live, then the streamers and commentators are required.
     * @param refereesLimit     the maximum number of referees that can be assigned to the match
     * @param commentatorsLimit the maximum number of commentators that can be assigned to the match
     * @param streamersLimit    the maximum number of streamers allowed to stream the match
     * @return A ParticipantVsMatch object
     */
    protected ParticipantVsMatch updateMatch(
            ParticipantVsMatch match,
            PlayerBasedTournament tournament,
            Long redParticipantId,
            Long blueParticipantId,
            LocalDate date,
            LocalTime time,
            List<Long> refereeIds,
            List<Long> commentatorIds,
            List<Long> streamerIds,
            Boolean isLive,
            Integer refereesLimit,
            Integer commentatorsLimit,
            Integer streamersLimit
    ) {
        if (refereeIds.size() > refereesLimit) throw new BadRequestException("Referees limit exceeded");
        if (commentatorIds.size() > commentatorsLimit) throw new BadRequestException("Commentators limit exceeded");
        if (streamerIds.size() > streamersLimit) throw new BadRequestException("Streamers limit exceeded");
        Participant redParticipant = participantService.getById(redParticipantId, tournament);
        Participant blueParticipant = participantService.getById(blueParticipantId, tournament);
        if (redParticipant.equals(blueParticipant)) throw new BadRequestException("Red and blue participants are the same!");
        List<StaffMember> referees = staffMemberService.getStaffMembersById(refereeIds, tournament);
        if (isLive) {
            List<StaffMember> commentators = staffMemberService.getStaffMembersById(commentatorIds, tournament);
            List<StaffMember> streamers = staffMemberService.getStaffMembersById(streamerIds, tournament);
            match.setCommentators(new HashSet<>(commentators));
            match.setStreamers(new HashSet<>(streamers));
        } else {
            match.setCommentators(new HashSet<>());
            match.setStreamers(new HashSet<>());
        }
        match.setRedParticipant(redParticipant);
        match.setBlueParticipant(blueParticipant);
        match.setStartDate(date);
        match.setStartTime(time);
        match.setRefereesLimit(refereesLimit);
        match.setCommentatorsLimit(commentatorsLimit);
        match.setStreamersLimit(streamersLimit);
        match.setReferees(new HashSet<>(referees));
        match.setIsLive(isLive);
        return matchRepository.save(match);
    }

    /**
     * It updates a match with the given data
     *
     * @param match the match to be updated
     * @param tournament The tournament the match is in.
     * @param redTeamId The id of the red team
     * @param blueTeamId The id of the blue team
     * @param date The date of the match
     * @param time LocalTime
     * @param refereeIds List of referee IDs
     * @param commentatorIds List of commentator IDs
     * @param streamerIds List of streamer IDs
     * @param isLive if the match is live, then the streamers and commentators are required.
     * @param refereesLimit the maximum number of referees that can be assigned to the match
     * @param commentatorsLimit The maximum number of commentators allowed for this match.
     * @param streamersLimit the maximum number of streamers allowed to stream the match
     * @return A TeamVsMatch object
     */
    protected TeamVsMatch updateMatch(
            TeamVsMatch match,
            TeamBasedTournament tournament,
            Long redTeamId,
            Long blueTeamId,
            LocalDate date,
            LocalTime time,
            List<Long> refereeIds,
            List<Long> commentatorIds,
            List<Long> streamerIds,
            Boolean isLive,
            Integer refereesLimit,
            Integer commentatorsLimit,
            Integer streamersLimit
    ) {
        if (refereeIds.size() > refereesLimit) throw new BadRequestException("Referees limit exceeded");
        if (commentatorIds.size() > commentatorsLimit) throw new BadRequestException("Commentators limit exceeded");
        if (streamerIds.size() > streamersLimit) throw new BadRequestException("Streamers limit exceeded");
        Team redTeam = teamService.getById(redTeamId, tournament);
        Team blueTeam = teamService.getById(blueTeamId, tournament);
        if (redTeam.equals(blueTeam)) throw new BadRequestException("Red and blue teams are the same!");
        List<StaffMember> referees = staffMemberService.getStaffMembersById(refereeIds, tournament);
        if (isLive) {
            List<StaffMember> commentators = staffMemberService.getStaffMembersById(commentatorIds, tournament);
            List<StaffMember> streamers = staffMemberService.getStaffMembersById(streamerIds, tournament);
            match.setCommentators(new HashSet<>(commentators));
            match.setStreamers(new HashSet<>(streamers));
        } else {
            match.setCommentators(new HashSet<>());
            match.setStreamers(new HashSet<>());
        }
        match.setRedTeam(redTeam);
        match.setBlueTeam(blueTeam);
        match.setStartDate(date);
        match.setStartTime(time);
        match.setRefereesLimit(refereesLimit);
        match.setCommentatorsLimit(commentatorsLimit);
        match.setStreamersLimit(streamersLimit);
        match.setReferees(new HashSet<>(referees));
        match.setIsLive(isLive);
        return matchRepository.save(match);
    }
}
