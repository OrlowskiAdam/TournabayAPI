package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.settings.TournamentSettings;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @NotNull
    @UpdateTimestamp
    protected LocalDateTime updatedAt;

    @NotNull
    protected LocalDateTime startDate;

    @NotNull
    protected LocalDateTime endDate;

    @JsonIgnore
    @OneToOne
    protected TournamentRole defaultRole;

    @JsonIgnore
    @OneToOne
    protected TournamentRole masterRole;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected GameMode gameMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected ScoreType scoreType;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected TeamFormat teamFormat;

    @OneToOne(mappedBy = "tournament")
    protected TournamentSettings tournamentSettings;

    @NotNull
    @ManyToOne
    protected User owner;

    @OneToMany(mappedBy = "tournament")
    protected List<StaffMember> staffMembers = new ArrayList<>();

    @OneToMany(mappedBy = "tournament")
    protected List<TournamentRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "tournament")
    private List<Participant> participants = new ArrayList<>();

    @OneToOne(mappedBy = "tournament")
    private Permission permission;

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
        return this.participants.stream().anyMatch(o -> o.getUser().getId().equals(participant.getUser().getId()));
    }

    public boolean containsStaffMember(StaffMember staffMember) {
        return this.staffMembers.stream().anyMatch(o -> o.getUser().getId().equals(staffMember.getUser().getId()));
    }
}
