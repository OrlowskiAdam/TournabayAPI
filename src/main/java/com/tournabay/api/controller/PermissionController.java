package com.tournabay.api.controller;

import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.payload.PermissionsRequest;
import com.tournabay.api.security.CurrentUser;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.service.PermissionService;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {
    private final TournamentService tournamentService;
    private final UserService userService;
    private final PermissionService permissionServiceImpl;

    @PatchMapping("/update/tournament/{tournamentId}")
    @Secured("ROLE_USER")
    @PreAuthorize("hasPermission(#tournamentId, 'Permissions')")
    public ResponseEntity<List<Permission>> updatePermissions(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long tournamentId, @RequestBody PermissionsRequest body) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        List<Permission> permissions = permissionServiceImpl.updatePermissions(body.getPermissionDtos(), tournament);
        return ResponseEntity.ok(permissions);
    }

}
