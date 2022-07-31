package com.tournabay.api.security;

import com.tournabay.api.exception.ForbiddenException;
import com.tournabay.api.model.*;
import com.tournabay.api.service.TournamentService;
import com.tournabay.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final UserService userService;
    private final TournamentService tournamentService;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }
        Long tournamentId = (Long) targetDomainObject;
        return hasTournamentPermission(auth, tournamentId, permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    /**
     * If the user is the owner of the tournament, or if the user is a staff member of the tournament and has a role that
     * is permitted to perform the action, or if the user is a staff member of the tournament and is specifically permitted
     * to perform the action, then the user has permission to perform the action
     *
     * @param auth         The Authentication object that Spring Security uses to store the user's credentials.
     * @param tournamentId The id of the tournament you want to check permissions for
     * @param permission   The permission to check for. This is the name of the method in the Permission class.
     * @return A boolean value.
     */
    private boolean hasTournamentPermission(Authentication auth, Long tournamentId, String permission) {
        try {
            if (!(auth.getPrincipal() instanceof UserPrincipal)) return false;
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            User user = userService.getUserFromPrincipal(userPrincipal);
            Tournament tournament = tournamentService.getTournamentById(tournamentId);
            if (tournament.getOwner().equals(user)) return true;
            Permission tournamentPermission = tournament.getPermission();
            Method tournamentRoleAccessMethod = tournamentPermission.getClass().getMethod("getCanTournamentRole" + permission);
            Method staffMemberAccessMethod = tournamentPermission.getClass().getMethod("getCanStaffMember" + permission);
            List<TournamentRole> permittedRoles = (List<TournamentRole>) tournamentRoleAccessMethod.invoke(tournamentPermission);
            List<StaffMember> permittedStaffMembers = (List<StaffMember>) staffMemberAccessMethod.invoke(tournamentPermission);
            StaffMember staffMember = tournament.getStaffMembers()
                    .stream()
                    .filter(s -> s.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow(ForbiddenException::new);
            if (permittedRoles.stream().anyMatch(tournamentRole -> staffMember.getTournamentRoles().contains(tournamentRole)))
                return true;
            if (permittedStaffMembers.stream().anyMatch(permittedStaffMember -> permittedStaffMember.equals(staffMember)))
                return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}