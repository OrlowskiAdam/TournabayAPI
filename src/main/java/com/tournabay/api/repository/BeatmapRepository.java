package com.tournabay.api.repository;

import com.tournabay.api.model.beatmap.Beatmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeatmapRepository extends JpaRepository<Beatmap, Long> {
    Optional<Beatmap> findByBeatmapModificationId(Long beatmapModificationId);
}
