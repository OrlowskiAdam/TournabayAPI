package com.tournabay.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@SuperBuilder
@AllArgsConstructor
public class TeamBasedTournament extends Tournament {

    @OneToMany
    private List<Team> teams;
}
