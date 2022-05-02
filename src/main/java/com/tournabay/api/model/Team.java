package com.tournabay.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Enumerated(EnumType.STRING)
    private Seed seed;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @OneToMany
    private Set<Participant> participants = new HashSet<>();
}
