package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.TournamentRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentRoleService {
    private final TournamentRoleRepository tournamentRoleRepository;

    /**
     * Save the tournamentRole object to the database.
     *
     * @param tournamentRole The tournamentRole object that you want to save.
     * @return The tournamentRole object is being returned.
     */
    public TournamentRole save(TournamentRole tournamentRole) {
        return tournamentRoleRepository.save(tournamentRole);
    }

    /**
     * Save all tournament roles to the database
     *
     * @param tournamentRoles The list of tournamentRoles to save.
     * @return A list of tournament roles.
     */
    public List<TournamentRole> saveAll(List<TournamentRole> tournamentRoles) {
        return tournamentRoleRepository.saveAll(tournamentRoles);
    }

    /**
     * This function is deprecated and should not be used
     *
     * @param ids The ids of the entities to retrieve.
     * @return A list of TournamentRole objects
     */
    @Deprecated
    public List<TournamentRole> getAllById(Iterable<Long> ids) {
        return tournamentRoleRepository.findAllById(ids);
    }

    /**
     * Get all roles from a tournament that have an id in the given list of ids.
     *
     * @param ids The ids of the roles you want to get
     * @param tournament The tournament that the roles belong to.
     * @return A list of TournamentRoles that are in the tournament and have an id that is in the list of ids.
     */
    public List<TournamentRole> getAllById(List<Long> ids, Tournament tournament) {
        return tournament.getRoles().stream().filter(role -> ids.contains(role.getId())).collect(Collectors.toList());
    }

    /**
     * Get the role with the given id from the given tournament, or throw a ResourceNotFoundException if it doesn't
     * exist.
     *
     * @param id The id of the role you want to get.
     * @param tournament The tournament that the role belongs to.
     * @return A TournamentRole object
     */
    public TournamentRole getById(Long id, Tournament tournament) {
        return tournament.getRoles()
                .stream()
                .filter(role -> role.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role not found!"));
    }

    /**
     * Get all the roles from the list of roles that have a name that is in the list of role names.
     *
     * @param roleNames A list of role names that you want to get from the tournamentRoles list.
     * @param tournamentRoles A list of TournamentRole objects
     * @return A list of TournamentRoles that have a name that is in the list of roleNames.
     */
    public List<TournamentRole> getRolesByName(List<String> roleNames, List<TournamentRole> tournamentRoles) {
        return tournamentRoles.stream().filter(role -> roleNames.contains(role.getName())).collect(Collectors.toList());
    }

    /**
     * Create a new role for a tournament, and set the position of the new role to be one higher than the highest position
     * of any role in the tournament.
     *
     * The first thing we do is get the highest position of any role in the tournament. We do this by calling the
     * `getRoleLastPosition` function, which we'll look at next
     *
     * @param roleName The name of the role.
     * @param isHidden If true, the role will not be visible to the user.
     * @param tournament The tournament that the role will be created for.
     * @return A new TournamentRole object
     */
    public TournamentRole createRole(String roleName, boolean isHidden, Tournament tournament) {
        int lastPosition = getRoleLastPosition(tournament);
        return tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false, isHidden, lastPosition + 1));
    }

    // TODO: Inheritance when permissions are done
    // TODO: docs
    public TournamentRole createRole(String roleName, Long inheritRoleId, boolean isHidden, Tournament tournament) {
        int lastPosition = getRoleLastPosition(tournament);
        TournamentRole tournamentRole = tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false, isHidden, lastPosition + 1));
        TournamentRole inheritRole = getById(inheritRoleId, tournament);
        return tournamentRole;
    }

    /**
     * It creates a list of tournament roles, adds them to the tournament, and saves them to the database
     *
     * @param tournament The tournament that the roles are being created for.
     * @return A list of tournament roles.
     */
    public List<TournamentRole> createDefaultTournamentRoles(Tournament tournament) {
        List<TournamentRole> tournamentRoles = new ArrayList<>();
        TournamentRole masterRole = new TournamentRole("Host", tournament, true, false, 1);
        tournamentRoles.add(masterRole);
        tournamentRoles.add(new TournamentRole("Organizer", tournament, false, false, 2));
        tournamentRoles.add(new TournamentRole("Pooler", tournament, false, false, 3));
        tournamentRoles.add(new TournamentRole("Referee", tournament, false, false, 4));
        tournamentRoles.add(new TournamentRole("Commentator", tournament, false, false, 5));
        tournamentRoles.add(new TournamentRole("Streamer", tournament, false, false, 6));
        TournamentRole defaultRole = new TournamentRole("Uncategorized", tournament, true, false, 7);
        tournamentRoles.add(defaultRole);
        tournament.setDefaultRole(defaultRole);
        tournament.setMasterRole(masterRole);
        return tournamentRoleRepository.saveAll(tournamentRoles);
    }

    /**
     * Update the tournament role with the given name and hidden status.
     *
     * @param tournamentRole The tournament role object that you want to update.
     * @param roleName The name of the role.
     * @param isHidden If true, the role will not be displayed in the tournament's role list.
     * @return The updated tournamentRole object.
     */
    public TournamentRole updateRole(TournamentRole tournamentRole, String roleName, Boolean isHidden) {
        tournamentRole.setName(roleName);
        tournamentRole.setIsHidden(isHidden);
        return tournamentRoleRepository.save(tournamentRole);
    }

    // TODO: detach roles from staff members
    public TournamentRole removeRole(TournamentRole tournamentRole, List<StaffMember> associatedStaffMembers, Tournament tournament) {
        tournamentRoleRepository.delete(tournamentRole);
        return tournamentRole;
    }

    /**
     * It returns the position of the last role in a tournament
     *
     * @param tournament The tournament object that the role is being added to.
     * @return The position of the last role in the tournament.
     */
    private int getRoleLastPosition(Tournament tournament) {
        List<TournamentRole> tournamentRoles = tournament.getRoles();
        tournamentRoles.sort(Comparator.comparing(TournamentRole::getPosition));
        return tournamentRoles.get(tournamentRoles.size() - 1).getPosition();
    }
}
