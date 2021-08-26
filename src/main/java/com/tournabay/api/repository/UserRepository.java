package com.tournabay.api.repository;

import com.tournabay.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDiscordId(String discordId);
    Optional<User> findByOsuId(Long osuId);
    Optional<User> findByUsername(String username);
}
