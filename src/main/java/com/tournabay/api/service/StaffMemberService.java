package com.tournabay.api.service;

import com.tournabay.api.exception.BadRequestException;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.exception.SecurityBreachException;
import com.tournabay.api.model.*;
import com.tournabay.api.repository.StaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffMemberService {
    private final StaffMemberRepository staffMemberRepository;
    private final UserService userService;
    private final TournamentRoleService tournamentRoleService;

    public StaffMember save(StaffMember staffMember) {
        return staffMemberRepository.save(staffMember);
    }

    public StaffMember getStaffMemberById(Long memberId, Tournament tournament) {
        return tournament
                .getStaffMembers()
                .stream()
                .filter(staffMember -> staffMember.getId().equals(memberId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
    }

    public StaffMember getStaffMemberByUser(User user, Tournament tournament) {
        return tournament
                .getStaffMembers()
                .stream()
                .filter(staffMember -> staffMember.getUser().getId().equals(user.getId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
    }

    public List<StaffMember> getStaffMembersById(List<Long> ids, Tournament tournament) {
        List<StaffMember> staffMembers = tournament.getStaffMembers();
        List<StaffMember> foundMembers = new ArrayList<>();
        for (StaffMember staffMember : staffMembers) {
            for (Long id : ids) {
                if (staffMember.getId().equals(id)) foundMembers.add(staffMember);
            }
        }
        if (foundMembers.size() != ids.size()) throw new SecurityBreachException("You shouldn't be here");
        return foundMembers;
    }

    public List<StaffMember> getStaffMembersByTournamentRole(TournamentRole tournamentRole, Tournament tournament) {
        List<TournamentRole> tournamentRoles = tournament.getRoles();
        if (!tournamentRoles.contains(tournamentRole))
            throw new BadRequestException("This role is not from this tournament");
        List<StaffMember> staffMembers = tournament.getStaffMembers();
        List<StaffMember> foundStaffMembers = new ArrayList<>();
        for (StaffMember staffMember : staffMembers) {
            List<TournamentRole> roles = staffMember.getTournamentRoles();
            for (TournamentRole role : roles) {
                if (role.getId().equals(tournamentRole.getId())) foundStaffMembers.add(staffMember);
            }
        }
        return foundStaffMembers;
    }

    public StaffMember addStaffMember(Long osuId, Tournament tournament, List<TournamentRole> tournamentRoles) {
        User user = userService.addUserByOsuId(osuId);
        if (tournamentRoles.isEmpty()) {
            tournamentRoles = new ArrayList<>();
            TournamentRole uncategorized = tournamentRoleService.getRoleByName("Uncategorized", tournament);
            tournamentRoles.add(uncategorized);
        }
        StaffMember staffMember = StaffMember.builder()
                .user(user)
                .status(StaffMemberStatus.JOINED) // TODO: Feature - allow / disable tournament invitations
                .tournamentRoles(tournamentRoles)
                .discordId(user.getDiscordId())
                .tournament(tournament)
                .build();
        if (!tournament.containsStaffMember(staffMember)) return staffMemberRepository.save(staffMember);
        throw new BadRequestException("This user is already a staff member!");
    }

    public StaffMember deleteStaffMember(StaffMember staffMember, Tournament tournament) {
        if (!tournament.containsStaffMember(staffMember))
            throw new BadRequestException("This user is not a staff member!");
        if (staffMember.getUser().getId().equals(tournament.getOwner().getId())) {
            throw new BadRequestException("The owner of this tournament cannot be removed!");
        }
        staffMemberRepository.delete(staffMember);
        return staffMember;
    }

    public List<StaffMember> deleteStaffMembers(List<StaffMember> staffMembers) {
        staffMemberRepository.deleteAll(staffMembers);
        staffMemberRepository.flush();
        return staffMembers;
    }

    public StaffMember updateStaffMember(StaffMember staffMember, List<TournamentRole> roles, String discordId, Tournament tournament) {
        try {
            if (discordId != null) {
                if (!discordId.equals("")) {
                    Long.parseLong(discordId);
                }
            }
            if (roles.isEmpty()) {
                List<TournamentRole> defaultRole = new ArrayList<>();
                TournamentRole uncategorizedRole = tournamentRoleService.getRoleByName("Uncategorized", tournament);
                defaultRole.add(uncategorizedRole);
                staffMember.setTournamentRoles(defaultRole);
            } else {
                staffMember.setTournamentRoles(roles);
            }
            staffMember.setDiscordId(discordId);
            return staffMemberRepository.save(staffMember);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Wrong discord ID format!");
        }
    }

    public List<StaffMember> disconnectTournamentRoleFromStaffMember(TournamentRole tournamentRole, List<StaffMember> associatedStaffMembers, Tournament tournament) {
        if (tournamentRole.getId().equals(tournament.getDefaultRole().getId()))
            throw new BadRequestException("You cannot remove default role!");
        for (StaffMember staffMember : associatedStaffMembers) {
            List<TournamentRole> roles = staffMember.getTournamentRoles();
            if (roles.size() == 1 && roles.get(0).getId().equals(tournamentRole.getId())) {
                roles.add(tournament.getDefaultRole());
            }
            roles.removeIf(role -> role.getId().equals(tournamentRole.getId()));
        }
        return staffMemberRepository.saveAll(associatedStaffMembers);
    }
}
