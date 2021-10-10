package com.tournabay.api.dto;

import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.TournamentRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TournamentRoleRemovalDto {
    private TournamentRole role;
    private List<StaffMember> staffMembers;
}
