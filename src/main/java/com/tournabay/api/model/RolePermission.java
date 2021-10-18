package com.tournabay.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class RolePermission extends Permission {

    @ManyToOne
    private TournamentRole tournamentRole;
}
