package com.tournabay.api.model.qualifications.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.beatmap.Beatmap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ParticipantScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Beatmap beatmap;
    private Long score;
    private Double accuracy;
    private Double qualificationPoints;

    @JsonIgnore
    @ManyToOne
    private QualificationResult result;
}
