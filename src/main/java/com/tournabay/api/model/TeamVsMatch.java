package com.tournabay.api.model;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@SuperBuilder
@Entity
@DynamicUpdate
@NoArgsConstructor
public class TeamVsMatch extends Match {

    @OneToOne
    private Team redTeam;

    @OneToOne
    private Team blueTeam;

    @OneToOne
    private Team winner;

    @OneToOne
    private Team loser;

    @ManyToOne
    private TeamBasedTournament tournament;
}
