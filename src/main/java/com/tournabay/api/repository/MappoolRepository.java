package com.tournabay.api.repository;

import com.tournabay.api.model.Mappool;
import com.tournabay.api.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappoolRepository extends JpaRepository<Mappool, Long> {
    List<Mappool> findAllByTournament(Tournament tournament);
}
