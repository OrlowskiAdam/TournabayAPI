package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.exception.SecurityBreachException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.TournamentRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentRoleService {
    private final TournamentRoleRepository tournamentRoleRepository;

    public TournamentRole save(TournamentRole tournamentRole) {
        return tournamentRoleRepository.save(tournamentRole);
    }

    @Deprecated
    public List<TournamentRole> getAllById(Iterable<Long> ids) {
        return tournamentRoleRepository.findAllById(ids);
    }

    public List<TournamentRole> getAllById(List<Long> ids, Tournament tournament) {
        return tournament.getRoles().stream().filter(role -> ids.contains(role.getId())).collect(Collectors.toList());
    }

    public TournamentRole getRoleById(Long id, Tournament tournament) {
        List<TournamentRole> roles = tournament.getRoles();
        for (TournamentRole role : roles) {
            if (role.getId().equals(id)) return role;
        }
        throw new ResourceNotFoundException("Role not found!");
    }

    public TournamentRole getRoleByName(String roleName, Tournament tournament) {
        List<TournamentRole> roles = tournament.getRoles();
        for (TournamentRole role : roles) {
            if (role.getName().equalsIgnoreCase(roleName)) return role;
        }
        throw new ResourceNotFoundException(roleName + " not found!");
    }

    public List<TournamentRole> getRolesByName(List<String> roleNames, Tournament tournament) {
        return tournament.getRoles().stream().filter(role -> roleNames.contains(role.getName())).collect(Collectors.toList());
    }

    public List<TournamentRole> getRolesByName(List<String> roleNames, List<TournamentRole> tournamentRoles) {
        return tournamentRoles.stream().filter(role -> roleNames.contains(role.getName())).collect(Collectors.toList());
    }

    public TournamentRole createRole(String roleName, boolean isHidden, Tournament tournament) {
        int lastPosition = getRoleLastPosition(tournament);
        TournamentRole tournamentRole = tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false, isHidden, lastPosition + 1));
        TournamentRole savedRole = save(tournamentRole);
        return savedRole;
    }

    // TODO: Inheritance when permissions are done
    public TournamentRole createRole(String roleName, Long inheritRoleId, boolean isHidden, Tournament tournament) {
        int lastPosition = getRoleLastPosition(tournament);
        TournamentRole tournamentRole = tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false, isHidden, lastPosition + 1));
        TournamentRole inheritRole = getRoleById(inheritRoleId, tournament);
        return tournamentRole;
    }

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

    public TournamentRole updateRole(TournamentRole tournamentRole, String roleName, Boolean isHidden) {
        tournamentRole.setName(roleName);
        tournamentRole.setIsHidden(isHidden);
        return tournamentRoleRepository.save(tournamentRole);
    }

    public TournamentRole removeRole(TournamentRole tournamentRole, List<StaffMember> associatedStaffMembers, Tournament tournament) {
        tournamentRoleRepository.delete(tournamentRole);
        return tournamentRole;
    }

    private int getRoleLastPosition(Tournament tournament) {
        List<TournamentRole> tournamentRoles = tournament.getRoles();
        tournamentRoles.sort(Comparator.comparing(TournamentRole::getPosition));
        return tournamentRoles.get(tournamentRoles.size() - 1).getPosition();
    }
}
