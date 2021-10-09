package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@SuperBuilder
public class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private StaffMemberStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<TournamentRole> tournamentRoles;

    private LocalDateTime joinedAt;

    private String discordId;

    @JsonIgnore
    @ManyToOne
    private Tournament tournament;

    @PrePersist
    private void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }
}
