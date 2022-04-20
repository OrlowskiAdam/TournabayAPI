package com.tournabay.api.repository;

import com.tournabay.api.model.settings.PlayerRegistrationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRegistrationSettingsRepository extends JpaRepository<PlayerRegistrationSettings, Long> {
}
