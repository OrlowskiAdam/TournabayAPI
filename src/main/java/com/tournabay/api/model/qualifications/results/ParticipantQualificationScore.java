package com.tournabay.api.model.qualifications.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.Participant;
import com.tournabay.api.model.beatmap.Beatmap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ParticipantQualificationScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score;
    private Double accuracy;

    @ManyToOne
    private Participant participant;

    @OneToOne
    private Beatmap beatmap;

    @JsonIgnore
    @ManyToOne
    private ParticipantQualificationResult participantQualificationResult;

    @JsonIgnore
    @ManyToOne
    private TeamQualificationResult teamQualificationResult;
}
