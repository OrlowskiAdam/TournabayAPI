package com.tournabay.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class RolePermission extends Permission {

    @ManyToOne
    private TournamentRole tournamentRole;
}
