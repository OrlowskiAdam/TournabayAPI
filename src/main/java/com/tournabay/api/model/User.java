package com.tournabay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="\"user\"")
@NoArgsConstructor
public class User {

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
            String osuToken,
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
        this.osuToken = osuToken;
        this.provider = provider;
        this.roles = roles;
        this.discordData = new HashSet<>();
    }

    public User(
            @NotBlank String username,
            @NotNull Long osuId,
            String avatarUrl,
            @NotNull AuthProvider provider,
            Set<Role> roles,
            int rank,
            int performancePoints,
            String countryCode
    ) {
        this.username = username;
        this.osuId = osuId;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
        this.roles = roles;
        this.rank = rank;
        this.performancePoints = performancePoints;
        this.countryCode = countryCode;
        this.discordData = new HashSet<>();
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

    @JsonIgnore
    @Lob
    private String osuToken;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiscordData> discordData = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).getId().equals(this.id);
    }
}
