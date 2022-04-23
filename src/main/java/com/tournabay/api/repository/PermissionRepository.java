package com.tournabay.api.repository;

import com.tournabay.api.model.Permission;
import com.tournabay.api.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByTournament(Tournament tournament);
}
