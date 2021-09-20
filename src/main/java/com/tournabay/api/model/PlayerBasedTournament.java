package com.tournabay.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@SuperBuilder
@AllArgsConstructor
public class PlayerBasedTournament extends Tournament {

    @OneToMany
    private List<Participant> players = new ArrayList<>();
}
