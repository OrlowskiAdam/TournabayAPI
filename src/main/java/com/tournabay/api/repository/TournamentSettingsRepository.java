package com.tournabay.api.repository;

import com.tournabay.api.model.settings.TournamentSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentSettingsRepository extends JpaRepository<TournamentSettings, Long> {
}
