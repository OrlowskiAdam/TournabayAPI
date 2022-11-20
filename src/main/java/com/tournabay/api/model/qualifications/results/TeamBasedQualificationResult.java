package com.tournabay.api.model.qualifications.results;

import com.tournabay.api.model.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class TeamBasedQualificationResult extends PlayerBasedQualificationResult {
    @OneToOne
    private Team team;
}
