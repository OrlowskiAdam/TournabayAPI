package com.tournabay.api.model;

import javax.persistence.*;

@Entity
public class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private TournamentRole tournamentRole;
}
