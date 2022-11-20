package com.tournabay.api.repository;

import com.tournabay.api.model.qualifications.results.ParticipantScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantScoreRepository extends JpaRepository<ParticipantScore, Long> {
}
