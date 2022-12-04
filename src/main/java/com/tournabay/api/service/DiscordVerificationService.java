package com.tournabay.api.service;

import com.tournabay.api.model.discord.DiscordVerification;
import org.springframework.stereotype.Service;

@Service
public interface DiscordVerificationService {

    DiscordVerification save(DiscordVerification discordVerification);

    DiscordVerification findByState(String state);

    void delete(DiscordVerification discordVerification);
}
