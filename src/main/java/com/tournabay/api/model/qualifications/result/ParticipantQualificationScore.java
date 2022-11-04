package com.tournabay.api.model.qualifications.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.Participant;
import com.tournabay.api.model.beatmap.Beatmap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@SuperBuilder
@Entity
@DynamicUpdate
@NoArgsConstructor
@Getter
@Setter
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
    private TeamQualificationScore teamQualificationScore;
}
