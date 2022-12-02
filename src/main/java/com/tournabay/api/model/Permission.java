package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@DynamicUpdate
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Tournament tournament;

    private String permissionName;

    @ManyToMany(mappedBy = "permissions")
    private List<TournamentRole> permittedRoles;

    @ManyToMany(mappedBy = "permissions")
    private List<StaffMember> permittedStaffMembers;

}
