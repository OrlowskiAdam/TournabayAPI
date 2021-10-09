package com.tournabay.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    @ManyToOne
    protected User owner;

    @OneToMany(mappedBy = "tournament")
    protected List<StaffMember> staffMembers = new ArrayList<>();

    @NotEmpty
    @OneToMany(mappedBy = "tournament")
    protected List<TournamentRole> roles = new ArrayList<>();

    @NotEmpty
    @OneToMany(mappedBy = "tournament")
    protected List<Page> pages;

    @OneToMany(mappedBy = "tournament")
    private List<Participant> players = new ArrayList<>();

    @PrePersist
    private void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean containsParticipant(Participant participant) {
        return this.players.stream().anyMatch(o -> o.getUser().getId().equals(participant.getUser().getId()));
    }

    public boolean containsStaffMember(StaffMember staffMember) {
        return this.staffMembers.stream().anyMatch(o -> o.getUser().getId().equals(staffMember.getUser().getId()));
    }
}
