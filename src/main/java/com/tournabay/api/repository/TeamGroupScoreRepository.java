package com.tournabay.api.repository;

import com.tournabay.api.model.TeamGroupScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamGroupScoreRepository extends JpaRepository<TeamGroupScore, Long> {
}
