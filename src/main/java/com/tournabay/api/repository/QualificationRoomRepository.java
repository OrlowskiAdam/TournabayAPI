package com.tournabay.api.repository;

import com.tournabay.api.model.qualifications.QualificationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationRoomRepository extends JpaRepository<QualificationRoom, Long> {
}
