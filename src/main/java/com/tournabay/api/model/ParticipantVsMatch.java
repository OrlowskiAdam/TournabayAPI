package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@SuperBuilder
@Entity
@DynamicUpdate
@NoArgsConstructor
@Getter
@Setter
public class ParticipantVsMatch extends Match {

    @OneToOne
    private Participant redParticipant;

    @OneToOne
    private Participant blueParticipant;

    @OneToOne
    private Participant winner;

    @OneToOne
    private Participant loser;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @ManyToOne
    private PlayerBasedTournament tournament;
}
