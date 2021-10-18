package com.tournabay.api.service;

import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.model.*;
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
    private final String[] securedPages = new String[]{
            "/dashboard/tournament/{tournamentId}",
            "/dashboard/tournament/{tournamentId}/staff",
            "/dashboard/tournament/{tournamentId}/security",
            "/dashboard/tournament/{tournamentId}/players"
    };

    public List<Page> saveAll(List<Page> pages) {
        return pageRepository.saveAll(pages);
    }

    public List<Page> createTournamentPages(List<TournamentRole> tournamentRoles, Tournament tournament) {
        List<Page> pages = new ArrayList<>();
        for (String securedPage : securedPages) {
            List<Permission> permissions = new ArrayList<>();
            for (TournamentRole tournamentRole : tournamentRoles) {
                Permission permission = permissionService.createRolePermission(true, true, tournamentRole);
                permissions.add(permission);
            }
            permissionService.saveAll(permissions);
            String path = securedPage.replace("{tournamentId}", tournament.getId().toString());
            Page page = Page
                    .builder()
                    .path(path)
                    .permissions(permissions)
                    .tournament(tournament)
                    .build();
            pages.add(page);
        }
        return pageRepository.saveAll(pages);
    }

    public List<Page> createRolePermissionForPage(TournamentRole tournamentRole, List<Page> pages, TournamentRole inheritRole) {
        List<Permission> permissions = new ArrayList<>();
        for (Page page : pages) {
            Permission permission = null;
            if (inheritRole != null) {
                List<Permission> pagePermissions = page.getPermissions();
                for (Permission pagePermission : pagePermissions) {
                    if (pagePermission instanceof RolePermission) {
                        if (((RolePermission) pagePermission).getTournamentRole().getId().equals(inheritRole.getId())) {
                            permission = permissionService.createRolePermission(pagePermission.getRead(), pagePermission.getWrite(), tournamentRole);
                            break;
                        }
                    }

                }
            } else {
                permission = permissionService.createRolePermission(false, false, tournamentRole);
            }
            permissions.add(permission);
            page.getPermissions().add(permission);
        }
        permissionService.saveAll(permissions);
        return pages;
    }

    public Page findPageById(Long pageId, Tournament tournament) {
        List<Page> pages = tournament.getPages();
        for (Page page : pages) {
            if (page.getId().equals(pageId)) return page;
        }
        throw new ResourceNotFoundException("Page not found");
    }
}
