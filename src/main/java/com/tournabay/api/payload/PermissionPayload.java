package com.tournabay.api.payload;

import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.TournamentRole;
import lombok.Getter;

import java.util.List;

@Getter
public class PermissionPayload {
    private List<TournamentRole> tournamentRoles;
    private List<StaffMember> staffMembers;
}
