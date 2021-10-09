package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TournamentRole {

    public TournamentRole(String name, Tournament tournament, boolean isProtected) {
        this.name = name;
        this.tournament = tournament;
        this.isProtected = isProtected;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Lob
    private String description;

    @NotNull
    private Boolean isProtected;

    @JsonIgnore
    @NotNull
    @ManyToOne
    private Tournament tournament;
}
