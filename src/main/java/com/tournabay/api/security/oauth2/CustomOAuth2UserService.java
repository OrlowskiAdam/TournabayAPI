package com.tournabay.api.security.oauth2;

import com.tournabay.api.exception.AppException;
import com.tournabay.api.exception.OAuth2AuthenticationProcessingException;
import com.tournabay.api.model.AuthProvider;
import com.tournabay.api.model.Role;
import com.tournabay.api.model.RoleName;
import com.tournabay.api.model.User;
import com.tournabay.api.repository.RoleRepository;
import com.tournabay.api.repository.UserRepository;
import com.tournabay.api.security.UserPrincipal;
import com.tournabay.api.security.oauth2.user.OAuth2UserInfo;
import com.tournabay.api.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        User user;
        if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equalsIgnoreCase("osu")) {
            Optional<User> userOptional = userRepository.findByOsuId(oAuth2UserInfo.getOsuId());
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                    throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                            user.getProvider() + " account. Please use your " + user.getProvider() +
                            " account to login.");
                }
                user = updateExistingUser(user, oAuth2UserInfo, oAuth2UserRequest);
            } else {
                user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
            }
            return UserPrincipal.create(user, oAuth2User.getAttributes());
        }
        return null;
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        User user = new User(
                oAuth2UserInfo.getUsername(),
                oAuth2UserInfo.getOsuId(),
                oAuth2UserInfo.getAvatarUrl(),
                oAuth2UserInfo.getCoverUrl(),
                oAuth2UserInfo.getCountryCode(),
                0,
                0,
                oAuth2UserInfo.pmFriendsOnly(),
                oAuth2UserInfo.isBot(),
                oAuth2UserRequest.getAccessToken().getTokenValue(),
                AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()),
                Collections.singleton(userRole)
        );
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, OAuth2UserRequest oAuth2UserRequest) {
        existingUser.setUsername(oAuth2UserInfo.getUsername());
        existingUser.setAvatarUrl(oAuth2UserInfo.getAvatarUrl());
        existingUser.setCountryCode(oAuth2UserInfo.getCountryCode());
        existingUser.setCoverUrl(oAuth2UserInfo.getCoverUrl());
        existingUser.setIsBot(oAuth2UserInfo.isBot());
        existingUser.setPmFriendsOnly(oAuth2UserInfo.pmFriendsOnly());
        existingUser.setOsuToken(oAuth2UserRequest.getAccessToken().getTokenValue());
        return userRepository.save(existingUser);
    }
}
