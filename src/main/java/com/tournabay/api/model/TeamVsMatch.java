package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@SuperBuilder
@Entity
@DynamicUpdate
@NoArgsConstructor
@Getter
@Setter
public class TeamVsMatch extends Match {

    @OneToOne(cascade = CascadeType.DETACH)
    private Team redTeam;

    @OneToOne(cascade = CascadeType.DETACH)
    private Team blueTeam;

    @OneToOne(cascade = CascadeType.DETACH)
    private Team winner;

    @OneToOne(cascade = CascadeType.DETACH)
    private Team loser;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @ManyToOne
    @NotNull
    private TeamBasedTournament tournament;
}
