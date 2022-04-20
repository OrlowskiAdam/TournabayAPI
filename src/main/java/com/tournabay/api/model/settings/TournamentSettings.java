package com.tournabay.api.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournabay.api.model.Tournament;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TournamentSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    private Tournament tournament;
}
