package com.tournabay.api.model.qualifications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class QualificationRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime startDate;

    private Character symbol;

    @OneToMany(mappedBy = "qualificationRoom", cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private List<StaffMember> staffMembers;

    @JsonIgnore
    @ManyToOne
    private Tournament tournament;

    @OneToMany(mappedBy = "qualificationRoom", cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private List<QualificationResult> results;

}
