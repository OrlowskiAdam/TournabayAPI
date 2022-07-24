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
public class ParticipantVsMatch extends Match {

    @OneToOne
    private Participant redParticipant;

    @OneToOne
    private Participant blueParticipant;

    @OneToOne
    private Participant winner;

    @OneToOne
    private Participant loser;

    @ManyToOne
    private PlayerBasedTournament tournament;
}
