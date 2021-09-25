package com.tournabay.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@Data
@SuperBuilder
public class PlayerBasedTournament extends Tournament {

}
