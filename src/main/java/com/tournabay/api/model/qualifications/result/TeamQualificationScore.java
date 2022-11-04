package com.tournabay.api.model.qualifications.result;

import com.tournabay.api.model.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@DynamicUpdate
@NoArgsConstructor
@Getter
@Setter
public class TeamQualificationScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team;

    @OneToMany(mappedBy = "teamQualificationScore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantQualificationScore> scores;
}
