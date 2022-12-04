package com.tournabay.api.security.oauth2.user;

import com.tournabay.api.exception.OAuth2AuthenticationProcessingException;
import com.tournabay.api.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.osu.toString())) {
            return new OsuOAuth2UserInfo(attributes);
//        } else if (registrationId.equalsIgnoreCase(AuthProvider.discord.toString())) {
//            return new DiscordOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported.");
        }
    }
}
