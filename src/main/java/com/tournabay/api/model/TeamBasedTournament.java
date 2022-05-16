package com.tournabay.api.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class TeamBasedTournament extends Tournament {

    @OneToMany(mappedBy = "tournament", orphanRemoval = true, cascade = {CascadeType.ALL})
    private List<Team> teams;
}
