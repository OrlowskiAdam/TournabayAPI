package com.tournabay.api.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class User {

    public User() {
    }

    public User(
            @NotBlank String username,
            @NotNull Long osuId,
            String avatarUrl,
            String coverUrl,
            String countryCode,
            int performancePoints,
            int rank,
            Boolean pmFriendsOnly,
            Boolean isBot,
            @NotNull AuthProvider provider,
            Set<Role> roles
    ) {
        this.username = username;
        this.osuId = osuId;
        this.avatarUrl = avatarUrl;
        this.coverUrl = coverUrl;
        this.countryCode = countryCode;
        this.performancePoints = performancePoints;
        this.rank = rank;
        this.pmFriendsOnly = pmFriendsOnly;
        this.isBot = isBot;
        this.provider = provider;
        this.roles = roles;
    }

    public User(
            String discordId,
            @NotNull AuthProvider provider,
            Set<Role> roles
    ) {
        this.username = "DiscordVerificationUser";
        this.osuId = -2L;
        this.discordId = discordId;
        this.provider = provider;
        this.roles = roles;
    }

    public User(
            @NotBlank String username,
            @NotNull Long osuId,
            String avatarUrl,
            @NotNull AuthProvider provider,
            Set<Role> roles
    ) {
        this.username = username;
        this.osuId = osuId;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
        this.roles = roles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    private Long osuId;

    private String discordId;

    private String avatarUrl;
    private String coverUrl;

    private String countryCode;
    private int performancePoints;
    private int rank;

    private Boolean pmFriendsOnly;

    private Boolean isBot;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
