package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @NotNull
    @OneToOne
    private Tournament tournament;

    @NotNull
    private Boolean openRank;

    @NotNull
    private Long minParticipantRank;

    @NotNull
    private Long maxParticipantRank;

    @NotNull
    private Integer baseTeamSize;

    @NotNull
    private Integer maxTeamSize;

    @NotNull
    private Boolean allowParticipantsRegistration;

    @NotNull
    private Boolean allowTeamsRegistration;

    public void validate() {
        if (minParticipantRank > maxParticipantRank) {
            throw new IllegalArgumentException("Minimum participant's rank must be lower than maximum participant's rank");
        }
        if (baseTeamSize > maxTeamSize) {
            throw new IllegalArgumentException("Base team size must be lower than maximum team size");
        }
    }
}
