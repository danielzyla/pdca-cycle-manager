package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.CheckPhase;
import io.github.danielzyla.pdcaApp.repository.CheckPhaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaCheckPhaseRepository extends CheckPhaseRepository, JpaRepository<CheckPhase, Long> {
    @Override
    CheckPhase save(CheckPhase entity);

    @Override
    Optional<CheckPhase> findById(Long id);
}
