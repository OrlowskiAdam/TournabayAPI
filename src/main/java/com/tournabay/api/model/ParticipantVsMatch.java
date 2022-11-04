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
public class ParticipantVsMatch extends Match {

    @OneToOne(cascade = CascadeType.DETACH)
    private Participant redParticipant;

    @OneToOne(cascade = CascadeType.DETACH)
    private Participant blueParticipant;

    @OneToOne(cascade = CascadeType.DETACH)
    private Participant winner;

    @OneToOne(cascade = CascadeType.DETACH)
    private Participant loser;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @ManyToOne
    @NotNull
    private PlayerBasedTournament tournament;

}
