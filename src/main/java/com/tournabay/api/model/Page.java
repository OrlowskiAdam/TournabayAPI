package com.tournabay.api.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String path;

    @OneToMany
    private List<TournamentRole> roles;

    @OneToMany
    private List<User> users;
}
