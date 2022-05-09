package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@DynamicUpdate
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    private Tournament tournament;

    // ROLES

    @OneToMany
    private List<TournamentRole> canTournamentRoleManageRoles;

    @OneToMany
    private List<StaffMember> canStaffMemberManageRoles;

    // STAFF MEMBERS

    @OneToMany
    private List<TournamentRole> canTournamentRoleManageStaffMembers;

    @OneToMany
    private List<StaffMember> canStaffMemberManageStaffMembers;

    // ACCESS

    @OneToMany
    private List<TournamentRole> canTournamentRoleManageAccess;

    @OneToMany
    private List<StaffMember> canStaffMemberManageAccess;

    // PARTICIPANTS

    @OneToMany
    private List<TournamentRole> canTournamentRoleManageParticipants;

    @OneToMany
    private List<StaffMember> canStaffMemberManageParticipants;

    // TEAMS

    @OneToMany
    private List<TournamentRole> canTournamentRoleManageTeams;

    @OneToMany
    private List<StaffMember> canStaffMemberManageTeams;

    // SETTINGS

    @OneToMany
    private List<TournamentRole> canTournamentRoleManageSettings;

    @OneToMany
    private List<StaffMember> canStaffMemberManageSettings;
}
