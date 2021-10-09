package com.tournabay.api.service;

import com.tournabay.api.model.Page;
import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.TournamentRole;
import com.tournabay.api.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final PermissionService permissionService;
    private static final String[] securedPages = new String[]{
            "/dashboard/tournament/{tournamentId}",
            "/dashboard/tournament/{tournamentId}/security",
            "/dashboard/tournament/{tournamentId}/players"
    };

    public List<Page> saveAll(List<Page> pages) {
        return pageRepository.saveAll(pages);
    }

    public List<Page> createTournamentPages(List<TournamentRole> tournamentRoles, Tournament tournament) {
        for (String securedPage : securedPages) {
            List<Permission> permissions = new ArrayList<>();
            for (TournamentRole tournamentRole : tournamentRoles) {
                Permission permission = permissionService.createRolePermission(true, true, tournamentRole);
                permissions.add(permission);
            }
            String path = securedPage.replace("{tournamentId}", tournament.getId().toString());
            Page page = Page
                    .builder()
                    .path(path)
                    .permissions(permissions)
                    .build();
        }

        return null;
    }
}
