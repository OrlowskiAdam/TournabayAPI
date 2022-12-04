package com.tournabay.api.repository;

import com.tournabay.api.model.DiscordData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordDataRepository extends JpaRepository<DiscordData, Long> {
    DiscordData findByDiscordId(String discordId);
    boolean existsByDiscordId(String discordId);
}
