package com.tournabay.api.repository;

import com.tournabay.api.model.BeatmapModification;
import com.tournabay.api.model.Mappool;
import com.tournabay.api.model.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeatmapModificationRepository extends JpaRepository<BeatmapModification, Long> {
    Optional<BeatmapModification> findByModificationAndMappool(Modification modification, Mappool mappoolId);
}
