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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffMemberService {
    private final StaffMemberRepository staffMemberRepository;
    private final UserService userService;
    private final TournamentRoleService tournamentRoleService;

    /**
     * Save the staff member to the database.
     *
     * @param staffMember The StaffMember object that is being saved.
     * @return The staffMember object is being returned.
     */
    public StaffMember save(StaffMember staffMember) {
        return staffMemberRepository.save(staffMember);
    }

    /**
     * Get the staff member with the given id from the given tournament, or throw a ResourceNotFoundException if it
     * doesn't exist.
     * <p>
     * The first thing we do is get the list of staff members from the tournament. Then we use the Stream API to filter the
     * list of staff members to only those with the given id. Then we use the findFirst() method to get the first staff
     * member in the list. If there is no staff member in the list, then we throw a ResourceNotFoundException
     *
     * @param memberId   The id of the staff member we want to retrieve
     * @param tournament The tournament that the staff member belongs to.
     * @return A StaffMember object
     */
    public StaffMember getStaffMemberById(Long memberId, Tournament tournament) {
        return tournament
                .getStaffMembers()
                .stream()
                .filter(staffMember -> staffMember.getId().equals(memberId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
    }

    /**
     * Get the staff member from the tournament that has the same user id as the user passed in.
     *
     * @param user       The user that is currently logged in.
     * @param tournament The tournament that the staff member is in
     * @return A StaffMember object
     */
    public StaffMember getStaffMemberByUser(User user, Tournament tournament) {
        return tournament
                .getStaffMembers()
                .stream()
                .filter(staffMember -> staffMember.getUser().getId().equals(user.getId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
    }

    /**
     * Get all staff members from the tournament that have an id in the list of ids.
     *
     * @param ids        A list of ids of the staff members you want to get.
     * @param tournament The tournament that the staff members belong to.
     * @return A list of staff members that are in the tournament and have an id that is in the list of ids.
     */
    public List<StaffMember> getStaffMembersById(List<Long> ids, Tournament tournament) {
        return tournament.getStaffMembers()
                .stream()
                .filter(staffMember -> ids.contains(staffMember.getId()))
                .collect(Collectors.toList());
    }

    /**
     * "Get all staff members that have a specific role in a tournament."
     * <p>
     * The first thing we do is check if the tournament has the role we're looking for. If it doesn't, we throw a
     * BadRequestException
     *
     * @param tournamentRole The role you want to get the staff members for
     * @param tournament     The tournament to get the staff members from
     * @return A list of staff members that have the given tournament role.
     */
    public List<StaffMember> getStaffMembersByTournamentRole(TournamentRole tournamentRole, Tournament tournament) {
        List<TournamentRole> tournamentRoles = tournament.getRoles();
        if (!tournamentRoles.contains(tournamentRole))
            throw new BadRequestException("This role is not from this tournament");
        return tournament.getStaffMembers()
                .stream()
                .filter(staffMember -> staffMember.getTournamentRoles().contains(tournamentRole))
                .collect(Collectors.toList());
    }

    /**
     * Add a staff member to a tournament.
     *
     * @param osuId           The osuId of the user you want to add to the tournament.
     * @param tournament      The tournament that the staff member is being added to
     * @param tournamentRoles A list of roles that the staff member will have.
     * @return A StaffMember object
     */
    public StaffMember addStaffMember(Long osuId, Tournament tournament, List<TournamentRole> tournamentRoles) {
        User user = userService.addUserByOsuId(osuId);
        if (tournamentRoles.isEmpty()) {
            tournamentRoles = new ArrayList<>();
            TournamentRole uncategorized = tournament.getDefaultRole();
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

    /**
     * If the staff member is not in the tournament, throw an exception. If the staff member is the owner of the
     * tournament, throw an exception. Otherwise, delete the staff member.
     *
     * @param staffMember The staff member to be deleted.
     * @param tournament  The tournament that the staff member is being removed from.
     * @return The staff member that was deleted.
     */
    public StaffMember deleteStaffMember(StaffMember staffMember, Tournament tournament) {
        if (!tournament.containsStaffMember(staffMember))
            throw new BadRequestException("This user is not a staff member!");
        if (staffMember.getUser().getId().equals(tournament.getOwner().getId())) {
            throw new BadRequestException("The owner of this tournament cannot be removed!");
        }
        staffMemberRepository.delete(staffMember);
        return staffMember;
    }

    /**
     * > This function deletes a list of staff members from the database
     *
     * @param staffMembers The list of staff members to be deleted.
     * @return A list of StaffMembers
     */
    @Deprecated
    public List<StaffMember> deleteStaffMembers(List<StaffMember> staffMembers) {
        staffMemberRepository.deleteAll(staffMembers);
        staffMemberRepository.flush();
        return staffMembers;
    }

    /**
     * It updates a staff member's roles and discord ID
     *
     * @param staffMember The staff member that is being updated
     * @param roles       a list of roles that the staff member has
     * @param discordId   The discord ID of the staff member.
     * @param tournament  The tournament that the staff member is being added to.
     * @return StaffMember
     */
    public StaffMember updateStaffMember(StaffMember staffMember, List<TournamentRole> roles, String discordId, Tournament tournament) {
        try {
            if (discordId != null) {
                if (!discordId.equals("")) {
                    Long.parseLong(discordId);
                }
            }
            if (roles.isEmpty()) {
                List<TournamentRole> defaultRole = new ArrayList<>();
                TournamentRole uncategorizedRole = tournament.getDefaultRole();
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

    /**
     * If the role is the default role, throw an exception. Otherwise, remove the role from the staff member's list of
     * roles, and if the staff member has no roles left, add the default role.
     *
     * @param tournamentRole         The role that is being disconnected from the staff members
     * @param associatedStaffMembers List of StaffMember objects that are associated with the tournamentRole
     * @param tournament             The tournament that the role is being removed from.
     * @return A list of staff members.
     */
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
