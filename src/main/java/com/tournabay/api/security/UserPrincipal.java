package com.tournabay.api.security;

import com.tournabay.api.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPrincipal implements OAuth2User, UserDetails {
    private Long id;
    private Long osuId;
    private String username;
    private String avatarUrl;
    private String coverUrl;
    private String discordId;

    private String countryCode;
    private Boolean pmFriendsOnly;
    private Boolean isBot;

    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id,
                         Long osuId,
                         String username,
                         String avatarUrl,
                         String coverUrl,
                         String discordId,
                         String countryCode,
                         Boolean pmFriendsOnly,
                         Boolean isBot,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.osuId = osuId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.coverUrl = coverUrl;
        this.discordId = discordId;
        this.countryCode = countryCode;
        this.pmFriendsOnly = pmFriendsOnly;
        this.isBot = isBot;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getOsuId(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.getCoverUrl(),
                user.getDiscordId(),
                user.getCountryCode(),
                user.getPmFriendsOnly(),
                user.getIsBot(),
                authorities
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public Long getId() {
        return id;
    }

    public Long getOsuId() {
        return osuId;
    }

    public String getDiscordId() {
        return discordId;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
