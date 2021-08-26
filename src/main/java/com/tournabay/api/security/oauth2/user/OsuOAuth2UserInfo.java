package com.tournabay.api.security.oauth2.user;

import java.util.Map;

public class OsuOAuth2UserInfo extends OAuth2UserInfo {

    public OsuOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Long getOsuId() {
        return ((Number) attributes.get("id")).longValue();
    }

    @Override
    public String getDiscordId() {
        return null;
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("username");
    }

    @Override
    public String getAvatarUrl() {
        return (String) attributes.get("avatar_url");
    }

    @Override
    public String getCoverUrl() {
        return (String) attributes.get("cover_url");
    }

    @Override
    public String getCountryCode() {
        return (String) attributes.get("country_code");
    }

    @Override
    public Boolean isBot() {
        return (Boolean) attributes.get("is_bot");
    }

    @Override
    public Boolean pmFriendsOnly() {
        return (Boolean) attributes.get("pm_friends_only");
    }
}
