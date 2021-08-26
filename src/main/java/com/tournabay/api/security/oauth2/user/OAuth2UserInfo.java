package com.tournabay.api.security.oauth2.user;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract Long getOsuId();

    public abstract String getDiscordId();

    public abstract String getUsername();

    public abstract String getAvatarUrl();

    public abstract String getCoverUrl();

    public abstract String getCountryCode();

    public abstract Boolean isBot();

    public abstract Boolean pmFriendsOnly();
}
