package com.tournabay.api.security.oauth2.user;

import java.util.Map;

public class DiscordOAuth2UserInfo extends OAuth2UserInfo {
    public DiscordOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Long getOsuId() {
        return null;
    }

    @Override
    public String getDiscordId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getAvatarUrl() {
        return null;
    }

    @Override
    public String getCoverUrl() {
        return null;
    }

    @Override
    public String getCountryCode() {
        return null;
    }

    @Override
    public Boolean isBot() {
        return null;
    }

    @Override
    public Boolean pmFriendsOnly() {
        return null;
    }
}
