package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.exception.SecurityBreachException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.TournamentRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentRoleService {
    private final TournamentRoleRepository tournamentRoleRepository;
    private final PermissionService permissionService;
    private final PageService pageService;

    public TournamentRole save(TournamentRole tournamentRole) {
        return tournamentRoleRepository.save(tournamentRole);
    }

    @Deprecated
    public List<TournamentRole> getAllById(Iterable<Long> ids) {
        return tournamentRoleRepository.findAllById(ids);
    }

    public List<TournamentRole> getAllById(List<Long> ids, Tournament tournament) {
        List<TournamentRole> tournamentRoles = tournament.getRoles();
        List<TournamentRole> foundRoles = new ArrayList<>();
        for (TournamentRole role : tournamentRoles) {
            for (Long id : ids) {
                if (role.getId().equals(id)) foundRoles.add(role);
            }
        }
        if (foundRoles.size() != ids.size()) throw new SecurityBreachException("");
        return foundRoles;
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

    public TournamentRole createRole(String roleName, boolean isHidden, Tournament tournament) {
        TournamentRole tournamentRole = tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false, isHidden));
        TournamentRole savedRole = save(tournamentRole);
        List<Page> pages = tournament.getPages();
        pageService.createRolePermissionForPage(tournamentRole, pages, null);
        pageService.saveAll(pages);
        return savedRole;
    }

    public TournamentRole createRole(String roleName, Long inheritRoleId, boolean isHidden, Tournament tournament) {
        TournamentRole tournamentRole = tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false, isHidden));
        TournamentRole inheritRole = getRoleById(inheritRoleId, tournament);
        List<Page> pages = tournament.getPages();
        pageService.createRolePermissionForPage(tournamentRole, pages, inheritRole);
        pageService.saveAll(pages);
        return tournamentRole;
    }

    public List<TournamentRole> createDefaultTournamentRoles(Tournament tournament) {
        List<TournamentRole> tournamentRoles = new ArrayList<>();
        TournamentRole masterRole = new TournamentRole("Host", tournament, true, false);
        tournamentRoles.add(masterRole);
        tournamentRoles.add(new TournamentRole("Organizer", tournament, false, false));
        tournamentRoles.add(new TournamentRole("Pooler", tournament, false, false));
        tournamentRoles.add(new TournamentRole("Referee", tournament, false, false));
        tournamentRoles.add(new TournamentRole("Commentator", tournament, false, false));
        tournamentRoles.add(new TournamentRole("Streamer", tournament, false, false));
        TournamentRole defaultRole = new TournamentRole("Uncategorized", tournament, true, false);
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
}
