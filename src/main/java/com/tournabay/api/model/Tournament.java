package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
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

    @OneToOne(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Settings settings;

    @NotNull
    @ManyToOne
    protected User owner;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    protected List<StaffMember> staffMembers = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    protected List<TournamentRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToOne(mappedBy = "tournament", cascade = CascadeType.ALL)
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

    @PostLoad
    private void onPostLoad() {
        this.getRoles().sort(Comparator.comparing(TournamentRole::getPosition));
    }

    public boolean containsParticipant(Participant participant) {
        return this.participants.stream().anyMatch(o -> o.getId().equals(participant.getId()));
    }

    public boolean containsParticipantById(Long id) {
        return this.participants.stream().anyMatch(o -> o.getId().equals(id));
    }

    public boolean containsStaffMember(StaffMember staffMember) {
        return this.staffMembers.stream().anyMatch(o -> o.getUser().getId().equals(staffMember.getUser().getId()));
    }
}
