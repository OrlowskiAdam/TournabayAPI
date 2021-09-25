package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TournamentRole {

    public TournamentRole(String role, Tournament tournament, boolean isProtected) {
        this.role = role;
        this.tournament = tournament;
        this.isProtected = isProtected;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String role;

    @Lob
    private String description;

    @NotNull
    private Boolean isProtected;

    @JsonIgnore
    @NotNull
    @ManyToOne
    private Tournament tournament;
}
