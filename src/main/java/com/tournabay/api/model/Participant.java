package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParticipantStatus participantStatus;

    @JsonIgnore
    @ManyToOne
    private Tournament tournament;

}
