package com.tournabay.api.model.qualifications;

import com.tournabay.api.model.StaffMember;
import com.tournabay.api.model.qualifications.result.QualificationResult;
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

    LocalDateTime startTime;

    @OneToMany(mappedBy = "qualificationRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    List<StaffMember> staffMembers;

    @OneToOne(mappedBy = "qualificationRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    QualificationResult qualificationResult;

}
