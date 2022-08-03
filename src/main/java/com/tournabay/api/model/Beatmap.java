package com.tournabay.api.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Beatmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
