package com.tournabay.api.model.qualifications;

import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.qualifications.results.QualificationResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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

    private LocalDateTime startTime;

    private Character symbol;

    @OneToMany(mappedBy = "qualificationRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffMember> staffMembers;

    @OneToMany(mappedBy = "qualificationRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QualificationResult> qualificationResults;

    @ManyToOne
    private Tournament tournament;

}
