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

    public TournamentRole getRoleByName(String roleName, Tournament tournament) {
        List<TournamentRole> roles = tournament.getRoles();
        for (TournamentRole role : roles) {
            if (role.getName().equalsIgnoreCase(roleName)) return role;
        }
        throw new ResourceNotFoundException(roleName + " not found!");
    }

    public TournamentRole createRole(String roleName, Tournament tournament) {
        return tournamentRoleRepository.save(new TournamentRole(roleName, tournament, false));
    }

    public TournamentRole createRole(String roleName, Long inheritRoleId, Tournament tournament) {
        TournamentRole tournamentRole = createRole(roleName, tournament);
        TournamentRole savedRole = save(tournamentRole);
        List<Page> pages = tournament.getPages();
        for (Page page : pages) {
            List<Permission> permissions = page.getPermissions();
            for (Permission permission : permissions) {
                if (permission instanceof RolePermission) {
                    TournamentRole existingRole = ((RolePermission) permission).getTournamentRole();
                    if (existingRole.getId().equals(inheritRoleId)) {
                        RolePermission newPermission = new RolePermission();
                        newPermission.setRead(permission.getRead());
                        newPermission.setWrite(permission.getWrite());
                        newPermission.setTournamentRole(savedRole);
                        permissions.add(newPermission);
                    }
                }
            }
            permissionService.saveAll(permissions);
        }
        pageService.saveAll(pages);
        return savedRole;
    }

    public List<TournamentRole> createDefaultTournamentRoles(Tournament tournament) {
        List<TournamentRole> tournamentRoles = new ArrayList<>();
        tournamentRoles.add(new TournamentRole("Host", tournament, true));
        tournamentRoles.add(new TournamentRole("Organizer", tournament, false));
        tournamentRoles.add(new TournamentRole("Pooler", tournament, false));
        tournamentRoles.add(new TournamentRole("Referee", tournament, false));
        tournamentRoles.add(new TournamentRole("Commentator", tournament, false));
        tournamentRoles.add(new TournamentRole("Streamer", tournament, false));
        tournamentRoles.add(new TournamentRole("Uncategorized", tournament, true));
        return tournamentRoleRepository.saveAll(tournamentRoles);
    }
}
