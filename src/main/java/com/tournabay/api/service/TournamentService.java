package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.payload.CreateTournamentRequest;
import com.tournabay.api.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final ParticipantService participantService;
    private final TournamentRoleService tournamentRoleService;
    private final TournamentSettingsService tournamentSettingsService;
    private final StaffMemberService staffMemberService;
    private final PermissionService permissionService;

    public Tournament getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tournament not found!"));
        tournament.getRoles().sort(Comparator.comparing(TournamentRole::getPosition));
        return tournament;
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Participant addParticipant(Tournament tournament, Participant participant) {
        if (!tournament.containsParticipant(participant)) {
            participant.setTournament(tournament);
            return participantService.save(participant);
        }
        throw new BadRequestException(participant.getUser().getUsername() + " is already a participant!");
    }

    // TODO: Code clean-up
    @Transactional
    public Tournament createTournament(CreateTournamentRequest body, User owner) {
        if (body.getTeamFormat().equals(TeamFormat.TEAM_VS)) {
            TeamBasedTournament tournament = TeamBasedTournament
                    .builder()
                    .name(body.getName())
                    .gameMode(body.getGameMode())
                    .scoreType(body.getScoreType())
                    .teamFormat(body.getTeamFormat())
                    .startDate(body.getStartDate())
                    .endDate(body.getEndDate())
                    .staffMembers(new ArrayList<>())
                    .roles(new ArrayList<>())
                    .owner(owner)
                    .build();
            Tournament newTournament = tournamentRepository.save(tournament);
            List<TournamentRole> defaultTournamentRoles = tournamentRoleService.createDefaultTournamentRoles(newTournament);
            staffMemberService.addStaffMember(
                    owner.getOsuId(),
                    newTournament,
                    defaultTournamentRoles.stream().filter(role -> role.getName().equals("Host")).collect(Collectors.toList())
            );
            permissionService.createDefaultPermission(newTournament, defaultTournamentRoles);
            return newTournament;
        } else if (body.getTeamFormat().equals(TeamFormat.PLAYER_VS)) {
            PlayerBasedTournament tournament = PlayerBasedTournament
                    .builder()
                    .name(body.getName())
                    .gameMode(body.getGameMode())
                    .scoreType(body.getScoreType())
                    .teamFormat(body.getTeamFormat())
                    .startDate(body.getStartDate())
                    .endDate(body.getEndDate())
                    .staffMembers(new ArrayList<>())
                    .roles(new ArrayList<>())
                    .owner(owner)
                    .build();
            Tournament newTournament = tournamentRepository.save(tournament);
            tournamentSettingsService.createDefaultRegistrationSettings(tournament);
            List<TournamentRole> defaultTournamentRoles = tournamentRoleService.createDefaultTournamentRoles(newTournament);
            staffMemberService.addStaffMember(owner.getOsuId(), newTournament, defaultTournamentRoles.stream().filter(role -> role.getName().equals("Host")).collect(Collectors.toList()));
            permissionService.createDefaultPermission(newTournament, defaultTournamentRoles);
            return newTournament;
        }

        throw new BadRequestException("Unsupported team format");
    }
}
