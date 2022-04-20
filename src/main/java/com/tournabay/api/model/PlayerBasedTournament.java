package com.tournabay.api.model;

import com.tournabay.api.model.settings.PlayerBasedTournamentSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class PlayerBasedTournament extends Tournament {
    @OneToOne(mappedBy = "tournament")
    private PlayerBasedTournamentSettings tournamentSettings;

}
