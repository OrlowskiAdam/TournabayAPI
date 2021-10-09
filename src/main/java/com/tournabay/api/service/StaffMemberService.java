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
    private final TournamentService tournamentService;
    private final TournamentRoleService tournamentRoleService;

    public StaffMember save(StaffMember staffMember) {
        return staffMemberRepository.save(staffMember);
    }

    public StaffMember getStaffMemberById(Long memberId, Tournament tournament) {
        List<StaffMember> staffMembers = tournament.getStaffMembers();
        for (StaffMember staffMember : staffMembers) {
            if (staffMember.getId().equals(memberId)) return staffMember;
        }
        throw new ResourceNotFoundException("Staff member not found for this tournament!");
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

    public StaffMember deleteStaffMember(StaffMember staffMember) {
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
}
