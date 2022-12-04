package com.tournabay.api.repository;

import com.tournabay.api.model.discord.DiscordVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordVerificationRepository extends JpaRepository<DiscordVerification, Long> {

    DiscordVerification findByState(String state);
}
