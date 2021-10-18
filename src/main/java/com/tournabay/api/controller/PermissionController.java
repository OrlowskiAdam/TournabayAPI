package com.tournabay.api.controller;

import com.tournabay.api.model.Page;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.UpdatePagePermissionsRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.PageService;
import com.tournabay.api.service.PermissionService;
import com.tournabay.api.service.TournamentRoleService;
import com.tournabay.api.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
public class PermissionController {
    private final TournamentRoleService tournamentRoleService;
    private final TournamentService tournamentService;
    private final PageService pageService;
    private final PermissionService permissionService;

    @PatchMapping("/update/{tournamentId}")
    @Secured("ROLE_USER")
    public ResponseEntity<List<Page>> updatePagePermissions(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody UpdatePagePermissionsRequest updatePagePermissionsRequest) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        Page page = pageService.findPageById(updatePagePermissionsRequest.getPageId(), tournament);
        permissionService.updatePermissions(page.getPermissions(), updatePagePermissionsRequest.getPermissions());
        return ResponseEntity.ok(tournament.getPages());
    }
}
