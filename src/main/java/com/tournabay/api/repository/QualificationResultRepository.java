package com.tournabay.api.repository;

import com.tournabay.api.model.qualifications.results.QualificationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationResultRepository extends JpaRepository<QualificationResult, Long> {
}
