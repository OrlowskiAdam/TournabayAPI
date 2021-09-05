package com.tournabay.api.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Tournament {

    public Tournament(@NotNull String name, @NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate, @NotNull ScoreType scoreType, @NotNull User owner) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scoreType = scoreType;
        this.owner = owner;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ScoreType scoreType;

    @ManyToOne
    @NotNull
    private User owner;

//    @OneToMany
//    private Set<User> hosts = new HashSet<>();
}
