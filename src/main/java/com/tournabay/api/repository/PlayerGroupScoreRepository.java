package com.tournabay.api.repository;

import com.tournabay.api.model.PlayerGroupScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerGroupScoreRepository extends JpaRepository<PlayerGroupScore, Long> {
}
