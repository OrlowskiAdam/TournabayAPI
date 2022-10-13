package com.tournabay.api.service;

import com.tournabay.api.model.beatmap.Stats;
import com.tournabay.api.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    /**
     * Save the beatmap stats to the database.
     *
     * @param stats The Stats object to be saved.
     * @return The Stats object is being returned.
     */
    public Stats save(Stats stats) {
        return statsRepository.save(stats);
    }
}
