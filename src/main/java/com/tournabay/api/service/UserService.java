package com.tournabay.api.service;

import com.tournabay.api.exception.OsuUserNotFound;
import com.tournabay.api.exception.ResourceNotFoundException;
import com.tournabay.api.jackson.user.UserReader;
import com.tournabay.api.model.AuthProvider;
import com.tournabay.api.model.Role;
import com.tournabay.api.model.User;
import com.tournabay.api.repository.UserRepository;
import com.tournabay.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public User getUserFromPrincipal(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User addUserByOsuId(Long osuId) {
        Optional<User> userOptional = userRepository.findByOsuId(osuId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        Set<Role> roles = roleService.getBasicRoles();
        UserReader userReader = new UserReader(osuId);
        if (!userReader.checkResponse()) throw new OsuUserNotFound("User with provided ID does not exists!");
        User user = new User(
                userReader.getUsername(),
                userReader.getOsuId(),
                "https://a.ppy.sh/" + userReader.getOsuId(),
                AuthProvider.osu,
                roles,
                userReader.getRank(),
                userReader.getPerformancePoints(),
                userReader.getCountry()

        );
        return userRepository.save(user);
    }
}
