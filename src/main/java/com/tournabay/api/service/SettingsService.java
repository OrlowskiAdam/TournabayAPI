package com.tournabay.api.service;

import com.tournabay.api.model.Settings;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final SettingsRepository settingsRepository;

    /**
     * Create a new settings object. It's used when a new tournament is created.
     *
     * @param tournament The tournament to which the settings belong.
     * @return Settings
     */
    @SuppressWarnings("UnusedReturnValue")
    public Settings createDefaultSettings(Tournament tournament) {
        Settings settings = Settings.builder()
                .tournament(tournament)
                .openRank(true)
                .minParticipantRank(1L)
                .maxParticipantRank(10000L)
                .baseTeamSize(4)
                .maxTeamSize(8)
                .allowParticipantsRegistration(false)
                .allowTeamsRegistration(false)
                .refereesLimit(1)
                .commentatorsLimit(2)
                .streamersLimit(1)
                .build();
        return settingsRepository.save(settings);
    }

    /**
     * Update the settings for a tournament.
     *
     * @param tournament The tournament that the settings are for.
     * @param settings The settings object that you want to update.
     * @return Settings
     */
    public Settings updateSettings(Tournament tournament, Settings settings) {
        settings.validate();
        settings.setTournament(tournament);
        return settingsRepository.save(settings);
    }

}
