package com.tournabay.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class PlayerBasedTournament extends Tournament {

}
