package com.tournabay.api.model.qualifications;

import com.tournabay.api.model.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class TeamBasedQualificationRoom extends QualificationRoom {

    @OneToMany(mappedBy = "qualificationRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;
}
