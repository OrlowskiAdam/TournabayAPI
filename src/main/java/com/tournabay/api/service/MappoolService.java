package com.tournabay.api.service;

import com.tournabay.api.model.Mappool;
import com.tournabay.api.model.Stage;
import com.tournabay.api.model.Tournament;
import com.tournabay.api.repository.MappoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MappoolService {
    private final MappoolRepository mappoolRepository;

    public List<Mappool> findAllByTournament(Tournament tournament) {
        return mappoolRepository.findAllByTournament(tournament);
    }

    public Mappool createMappool(Tournament tournament, Stage stage, String name) {
        Mappool mappool = Mappool.builder()
                .tournament(tournament)
                .stage(stage)
                .name(name)
                .build();
        return mappoolRepository.save(mappool);
    }
}
