package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime joinedAt;

    @ManyToOne
    private User user;

    private String discordId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    @Transient
    private String teamName;

    @Transient
    private Long teamId;

    @JsonIgnore
    @ManyToOne
    private Tournament tournament;

    @PostLoad
    public void postLoad() {
        if (this.tournament instanceof TeamBasedTournament teamBasedTournament) {
            teamBasedTournament
                    .getTeams()
                    .stream()
                    .filter(team -> team.getParticipants().stream().anyMatch(participant -> participant.getId().equals(this.id)))
                    .findFirst()
                    .ifPresent(team -> {
                        this.teamName = team.getName();
                        this.teamId = team.getId();
                    });
        }
    }

}
