package com.tournabay.api.model.qualifications.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.qualifications.QualificationRoom;
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
public abstract class QualificationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Tournament tournament;

    @JsonIgnore
    @ManyToOne
    private QualificationRoom qualificationRoom;
}
