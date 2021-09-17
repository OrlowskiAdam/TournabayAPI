package com.tournabay.api.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToMany
    private List<TournamentRole> tournamentRole;
}
