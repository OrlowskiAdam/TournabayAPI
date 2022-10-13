package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
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

    @JsonIgnore
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

    @NotNull
    @Min(value = 1, message = "Referees limit must be greater than 0")
    private Integer refereesLimit;

    @NotNull
    @Min(value = 1, message = "Commentators limit must be greater than 0")
    private Integer commentatorsLimit;

    @NotNull
    @Min(value = 1, message = "Streamers limit must be greater than 0")
    private Integer streamersLimit;

    public void validate() {
        if (minParticipantRank > maxParticipantRank) {
            throw new IllegalArgumentException("Minimum participant's rank must be lower than maximum participant's rank");
        }
        if (baseTeamSize > maxTeamSize) {
            throw new IllegalArgumentException("Base team size must be lower than maximum team size");
        }
        if (refereesLimit <= 0) {
            throw new IllegalArgumentException("Referees limit must be greater than 0");
        }
        if (commentatorsLimit <= 0) {
            throw new IllegalArgumentException("Commentators limit must be greater than 0");
        }
        if (streamersLimit <= 0) {
            throw new IllegalArgumentException("Streamers limit must be greater than 0");
        }
    }
}
