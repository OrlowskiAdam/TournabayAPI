package com.tournabay.api.repository;

import com.tournabay.api.model.beatmap.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
}
