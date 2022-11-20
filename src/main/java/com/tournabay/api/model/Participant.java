package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tournabay.api.model.qualifications.PlayerBasedQualificationRoom;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime joinedAt;

    @NotNull
    @ManyToOne
    private User user;

    private String discordId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    @Enumerated(EnumType.STRING)
    private Seed seed;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "name"
    )
    @ManyToOne
    private Team team;

    @JsonIgnore
    @ManyToOne
    private PlayerBasedQualificationRoom qualificationRoom;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @ManyToOne
    private Tournament tournament;

    @PreRemove
    private void preRemove() {
        if (team != null) {
            team.getParticipants().remove(this);
        }
        if (tournament != null) {
            tournament.getParticipants().remove(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Participant && ((Participant) obj).getId().equals(id);
    }
}
