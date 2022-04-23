package com.tournabay.api.service;

import com.tournabay.api.model.PlayerBasedTournament;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.model.settings.PlayerBasedTournamentSettings;
import com.tournabay.api.model.settings.PlayerRegistrationSettings;
import com.tournabay.api.model.settings.TournamentSettings;
import com.tournabay.api.payload.UpdateRegistrationSettingsRequest;
import com.tournabay.api.repository.PlayerRegistrationSettingsRepository;
import com.tournabay.api.repository.TournamentSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentSettingsService {
    private final TournamentSettingsRepository tournamentSettingsRepository;
    private final PlayerRegistrationSettingsRepository playerRegistrationSettingsRepository;

    public TournamentSettings createDefaultRegistrationSettings(Tournament tournament) {
        if (tournament instanceof PlayerBasedTournament) {
            PlayerBasedTournamentSettings settings = new PlayerBasedTournamentSettings();
            PlayerRegistrationSettings registrationSettings = PlayerRegistrationSettings
                    .builder()
                    .allowRegistration(false)
                    .openRank(false)
                    .maxRank(0L)
                    .minRank(25000L)
                    .build();
            PlayerRegistrationSettings savedRegistrationSettings = playerRegistrationSettingsRepository.save(registrationSettings);
            settings.setTournament(tournament);
            settings.setRegistrationSettings(savedRegistrationSettings);
            return tournamentSettingsRepository.save(settings);
        }
        throw new UnsupportedOperationException();
    }

    public TournamentSettings updateRegistrationSettings(Tournament tournament, UpdateRegistrationSettingsRequest body) {
        if (tournament instanceof PlayerBasedTournament) {
            TournamentSettings tournamentSettings = tournament.getTournamentSettings();
            if (tournamentSettings instanceof PlayerBasedTournamentSettings) {
                PlayerRegistrationSettings playerRegistrationSettings = ((PlayerBasedTournamentSettings) tournamentSettings).getRegistrationSettings();
                playerRegistrationSettings.setAllowRegistration(body.getAllowRegistration());
                if (!playerRegistrationSettings.getAllowRegistration()) playerRegistrationSettings.setOpenRank(false);
                else playerRegistrationSettings.setOpenRank(body.getOpenRank());
                playerRegistrationSettings.setMaxRank(body.getMaxRank());
                playerRegistrationSettings.setMinRank(body.getMinRank());
                playerRegistrationSettingsRepository.save(playerRegistrationSettings);
                return tournamentSettings;
            }
        }
        throw new UnsupportedOperationException();
    }
}
