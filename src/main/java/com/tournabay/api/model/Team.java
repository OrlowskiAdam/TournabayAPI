package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Enumerated(EnumType.STRING)
    private Seed seed;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @OneToMany(mappedBy = "team")
    private Set<Participant> participants = new HashSet<>();

    @OneToOne
    private Participant captain;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @ManyToOne
    private TeamBasedTournament tournament;

    @PrePersist
    private void prePersist() {
        if (this.seed == null) {
            this.seed = Seed.UNKNOWN;
        }
        if (this.status == null) {
            this.status = TeamStatus.UNKNOWN;
        }
        participants.forEach(participant -> participant.setTeam(this));
    }

    @PreRemove
    private void removeParticipants() {
        participants.forEach(participant -> participant.setTeam(null));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && ((Team) obj).getId().equals(this.getId());
    }
}
