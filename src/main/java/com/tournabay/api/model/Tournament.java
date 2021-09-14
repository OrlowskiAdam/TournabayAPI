package com.tournabay.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@SuperBuilder
@NoArgsConstructor
@Data
public abstract class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    protected String name;

    @NotNull
    protected LocalDateTime createdAt;

    @NotNull
    protected LocalDateTime updatedAt;

    @NotNull
    protected LocalDateTime startDate;

    @NotNull
    protected LocalDateTime endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected GameMode gameMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected ScoreType scoreType;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected TeamFormat teamFormat;

    @ManyToOne
    @NotNull
    protected User owner;

    @PrePersist
    private void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

//    @OneToMany
//    protected Set<User> hosts = new HashSet<>();
}
