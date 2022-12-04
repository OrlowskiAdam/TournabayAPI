package com.tournabay.api.service.implementation;

import com.tournabay.api.model.discord.DiscordVerification;
import com.tournabay.api.repository.DiscordVerificationRepository;
import com.tournabay.api.service.DiscordVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordVerificationServiceImpl implements DiscordVerificationService {
    private final DiscordVerificationRepository discordVerificationRepository;

    @Override
    public DiscordVerification save(DiscordVerification discordVerification) {
        return discordVerificationRepository.save(discordVerification);
    }

    @Override
    public DiscordVerification findByState(String state) {
        return discordVerificationRepository.findByState(state);
    }

    @Override
    public void delete(DiscordVerification discordVerification) {
        discordVerificationRepository.delete(discordVerification);
    }
}
