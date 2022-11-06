package com.tournabay.api.service.implementation;

import com.tournabay.api.exception.*;
import com.tournabay.api.model.*;
import com.tournabay.api.model.qualifications.PlayerBasedQualificationRoom;
import com.tournabay.api.model.qualifications.QualificationRoom;
import com.tournabay.api.model.qualifications.TeamBasedQualificationRoom;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import com.tournabay.api.payload.UpdatePlayerBasedQualificationRoomRequest;
import com.tournabay.api.payload.UpdateQualificationRoomRequest;
import com.tournabay.api.payload.UpdateTeamBasedQualificationRoomRequest;
import com.tournabay.api.repository.QualificationResultRepository;
import com.tournabay.api.repository.QualificationRoomRepository;
import com.tournabay.api.service.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualificationRoomServiceImpl implements QualificationRoomService {
    private final TournamentService tournamentService;
    private final QualificationRoomRepository qualificationRoomRepository;
    private final QualificationResultRepository qualificationResultRepository;
    private final TeamService teamService;
    private final StaffMemberService staffMemberService;
    private final ParticipantService participantService;

    @Override
    public QualificationRoom createQualificationRoom(LocalDateTime startTime, Tournament tournament) {
        Character symbol = this.getNewSymbol(tournament);
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournament playerBasedTournament = (PlayerBasedTournament) tournament;
            QualificationRoom qualificationRoom = PlayerBasedQualificationRoom.builder()
                    .startTime(startTime)
                    .symbol(symbol)
                    .staffMembers(new ArrayList<>())
                    .qualificationResults(new ArrayList<>())
                    .participants(new ArrayList<>())
                    .tournament(playerBasedTournament)
                    .build();
            return qualificationRoomRepository.save(qualificationRoom);
        } else if (tournament instanceof TeamBasedTournament) {
            TeamBasedTournament teamBasedTournament = (TeamBasedTournament) tournament;
            QualificationRoom qualificationRoom = TeamBasedQualificationRoom.builder()
                    .startTime(startTime)
                    .symbol(symbol)
                    .staffMembers(new ArrayList<>())
                    .qualificationResults(new ArrayList<>())
                    .teams(new ArrayList<>())
                    .tournament(teamBasedTournament)
                    .build();
            return qualificationRoomRepository.save(qualificationRoom);
        }
        throw new IncorrectTournamentType("The tournament type is not supported");
    }

    @Override
    public QualificationRoom removeQualificationRoom(QualificationRoom qualificationRoom, Tournament tournament) {
        tournament.getQualificationRooms().remove(qualificationRoom);
        qualificationRoom.setTournament(null);
        tournamentService.save(tournament);
        qualificationRoomRepository.delete(qualificationRoom);
        return qualificationRoom;
    }

    @Override
    public QualificationRoom getQualificationRoom(Long id, Tournament tournament) {
        return tournament
                .getQualificationRooms()
                .stream()
                .filter(qualificationRoom -> qualificationRoom.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Qualification room not found!"));
    }

    /**
     * If the tournament is team based, then the request must be a team based request, and the teams must not be in another
     * room. If the tournament is player based, then the request must be a player based request, and the participants must
     * not be in another room
     *
     * @param tournament        The tournament that the qualification room belongs to.
     * @param qualificationRoom The qualification room to be updated.
     * @param request           The request object that contains the data that will be used to update the qualification room.
     * @return A QualificationRoom object.
     */
    @Override
    public QualificationRoom updateQualificationRoom(Tournament tournament, QualificationRoom qualificationRoom, UpdateQualificationRoomRequest request) {
        if (tournament instanceof TeamBasedTournament &&
                qualificationRoom instanceof TeamBasedQualificationRoom &&
                request instanceof UpdateTeamBasedQualificationRoomRequest
        ) {
            UpdateTeamBasedQualificationRoomRequest updateTeamBasedQualificationRoomRequest =
                    (UpdateTeamBasedQualificationRoomRequest) request;
            List<Team> teams = teamService.getAllByIds(updateTeamBasedQualificationRoomRequest.getTeamIds(), tournament);
            boolean isTeamInOtherRoom = teams.stream()
                    .anyMatch(team -> team.getQualificationRoom() != null && !team.getQualificationRoom().equals(qualificationRoom));
            if (isTeamInOtherRoom)
                throw new BadRequestException("One or more teams are already in another qualification room!");
            List<StaffMember> staffMembers = staffMemberService.getStaffMembersById(updateTeamBasedQualificationRoomRequest.getStaffMemberIds(), tournament);
            TeamBasedQualificationRoom teamBasedQualificationRoom = (TeamBasedQualificationRoom) qualificationRoom;
            teamBasedQualificationRoom.setStartTime(updateTeamBasedQualificationRoomRequest.getStartTime());
            teamBasedQualificationRoom.setTeams(teams);
            teamBasedQualificationRoom.setStaffMembers(staffMembers);
            return qualificationRoomRepository.save(teamBasedQualificationRoom);
        } else if (tournament instanceof PlayerBasedTournament &&
                qualificationRoom instanceof PlayerBasedQualificationRoom
        ) {
            UpdatePlayerBasedQualificationRoomRequest updatePlayerBasedQualificationRoomRequest =
                    (UpdatePlayerBasedQualificationRoomRequest) request;
            List<Participant> participants = participantService.getAllByIds(updatePlayerBasedQualificationRoomRequest.getParticipantIds(), tournament);
            boolean isParticipantInOtherRoom = participants.stream()
                    .anyMatch(participant -> participant.getQualificationRoom() != null && !participant.getQualificationRoom().equals(qualificationRoom));
            if (isParticipantInOtherRoom)
                throw new BadRequestException("One or more participants are already in another qualification room!");
            List<StaffMember> staffMembers = staffMemberService.getStaffMembersById(updatePlayerBasedQualificationRoomRequest.getStaffMemberIds(), tournament);
            PlayerBasedQualificationRoom playerBasedQualificationRoom = (PlayerBasedQualificationRoom) qualificationRoom;
            playerBasedQualificationRoom.setStartTime(request.getStartTime());
            playerBasedQualificationRoom.setParticipants(participants);
            playerBasedQualificationRoom.setStaffMembers(staffMembers);
            return qualificationRoomRepository.save(playerBasedQualificationRoom);
        }
        throw new BadRequestException("The request is not valid!");
    }

    @Override
    public QualificationRoom addStaffMember(QualificationRoom qualificationRoom, StaffMember staffMember) {
        qualificationRoom.getStaffMembers().add(staffMember);
        return qualificationRoomRepository.save(qualificationRoom);
    }

    @Override
    public QualificationRoom removeStaffMember(QualificationRoom qualificationRoom, StaffMember staffMember) {
        qualificationRoom.getStaffMembers().remove(staffMember);
        return qualificationRoomRepository.save(qualificationRoom);
    }

    @Override
    public List<QualificationRoom> getQualificationRooms(Tournament tournament) {
        return tournament.getQualificationRooms();
    }

    @Override
    public QualificationRoom submitResult(QualificationRoom qualificationRoom) {
        throw new NotYetImplementedException();
    }

    @Override
    public QualificationRoom assignTeam(QualificationRoom qualificationRoom, Team team) {
        boolean isTeamInOtherRoom = team.getQualificationRoom() != null && !team.getQualificationRoom().equals(qualificationRoom);
        if (isTeamInOtherRoom) throw new ResourceNotFoundException("Team is already in another qualification room!");
        if (qualificationRoom instanceof TeamBasedQualificationRoom) {
            TeamBasedQualificationRoom teamBasedQualificationRoom = (TeamBasedQualificationRoom) qualificationRoom;
            teamBasedQualificationRoom.getTeams().add(team);
            team.setQualificationRoom(teamBasedQualificationRoom);
            return qualificationRoomRepository.save(teamBasedQualificationRoom);
        }
        throw new IncorrectQualificationRoomType("Qualification room is not a team based qualification room!");
    }

    @Override
    public QualificationRoom assignParticipant(QualificationRoom qualificationRoom, Participant participant) {
        boolean isParticipantInOtherRoom = participant.getQualificationRoom() != null && !participant.getQualificationRoom().equals(qualificationRoom);
        if (isParticipantInOtherRoom)
            throw new ResourceNotFoundException("Participant is already in another qualification room!");
        if (qualificationRoom instanceof PlayerBasedQualificationRoom) {
            PlayerBasedQualificationRoom playerBasedQualificationRoom = (PlayerBasedQualificationRoom) qualificationRoom;
            playerBasedQualificationRoom.getParticipants().add(participant);
            participant.setQualificationRoom(playerBasedQualificationRoom);
            return qualificationRoomRepository.save(playerBasedQualificationRoom);
        }
        throw new IncorrectQualificationRoomType("Qualification room is not a player based qualification room!");
    }

    @Override
    public QualificationRoom removeResult(QualificationRoom qualificationRoom, QualificationResult qualificationResult) {
        qualificationRoom.getQualificationResults().remove(qualificationResult);
        qualificationResult.setQualificationRoom(null);
        QualificationRoom savedQualificationRoom = qualificationRoomRepository.save(qualificationRoom);
        qualificationResultRepository.delete(qualificationResult);
        return savedQualificationRoom;
    }

    @Override
    public Character getNewSymbol(Tournament tournament) {
        Character roomSymbol = 'A';
        List<QualificationRoom> rooms = tournament.getQualificationRooms();
        for (QualificationRoom room : rooms) {
            if (room.getSymbol().equals(roomSymbol)) {
                roomSymbol++;
            }
        }
        return roomSymbol;
    }

}
