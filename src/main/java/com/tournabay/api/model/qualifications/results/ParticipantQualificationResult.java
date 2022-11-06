package com.tournabay.api.model.qualifications.results;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ParticipantQualificationResult extends QualificationResult {

    @OneToMany(mappedBy = "participantQualificationResult", orphanRemoval = true, cascade = {CascadeType.ALL})
    private List<ParticipantQualificationScore> scores;
}
