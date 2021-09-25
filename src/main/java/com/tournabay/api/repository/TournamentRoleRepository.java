package com.tournabay.api.repository;

import com.tournabay.api.model.TournamentRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRoleRepository extends JpaRepository<TournamentRole, Long> {
}
