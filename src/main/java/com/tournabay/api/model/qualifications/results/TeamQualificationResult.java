package com.tournabay.api.model.qualifications.results;

import com.tournabay.api.model.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class TeamQualificationResult extends QualificationResult {

    @ManyToOne
    private Team team;

    @OneToMany(mappedBy = "teamQualificationResult", orphanRemoval = true, cascade = {javax.persistence.CascadeType.ALL})
    private List<ParticipantQualificationScore> scores;
}
