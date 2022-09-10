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
    private final TournamentRoleService tournamentRoleService;
    private final StaffMemberService staffMemberService;
    private final PermissionService permissionService;
    private final SettingsService settingsService;

    /**
     * Get the tournament with the given id, or throw an exception if it doesn't exist.
     *
     * @param id The id of the tournament you want to get.
     * @return A tournament object with the roles sorted by position.
     */
    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tournament not found!"));
    }

    /**
     * Save the tournament to the database.
     *
     * @param tournament The tournament object that is being saved.
     * @return The tournament object is being returned.
     */
    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    // TODO: Code clean-up
    /**
     * It creates a tournament from request body.
     *
     * @param body The request body, which is a CreateTournamentRequest object.
     * @param owner The user who created the tournament
     * @return A tournament object
     */
    @Transactional
    public Tournament createTournament(CreateTournamentRequest body, User owner) {
        if (body.getTeamFormat().equals(TeamFormat.TEAM_VS)) {
            TeamBasedTournament tournament = TeamBasedTournament
                    .builder()
                    .name(body.getName())
                    .gameMode(body.getGameMode())
                    .scoreType(body.getScoreType())
                    .teamFormat(body.getTeamFormat())
                    .maxStage(body.getMaxStage())
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
            settingsService.createDefaultSettings(newTournament);
            return newTournament;
        } else if (body.getTeamFormat().equals(TeamFormat.PLAYER_VS)) {
            PlayerBasedTournament tournament = PlayerBasedTournament
                    .builder()
                    .name(body.getName())
                    .gameMode(body.getGameMode())
                    .scoreType(body.getScoreType())
                    .teamFormat(body.getTeamFormat())
                    .maxStage(body.getMaxStage())
                    .startDate(body.getStartDate())
                    .endDate(body.getEndDate())
                    .staffMembers(new ArrayList<>())
                    .roles(new ArrayList<>())
                    .owner(owner)
                    .build();
            Tournament newTournament = tournamentRepository.save(tournament);
            settingsService.createDefaultSettings(tournament);
            List<TournamentRole> defaultTournamentRoles = tournamentRoleService.createDefaultTournamentRoles(newTournament);
            staffMemberService.addStaffMember(owner.getOsuId(), newTournament, defaultTournamentRoles.stream().filter(role -> role.getName().equals("Host")).collect(Collectors.toList()));
            permissionService.createDefaultPermission(newTournament, defaultTournamentRoles);
            return newTournament;
        }

        throw new BadRequestException("Unsupported team format");
    }
}
