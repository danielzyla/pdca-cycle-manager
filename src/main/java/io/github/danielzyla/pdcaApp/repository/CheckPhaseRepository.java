package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.CheckPhase;

import java.util.Optional;

public interface CheckPhaseRepository {
    CheckPhase save(CheckPhase entity);
    Optional<CheckPhase> findById(Long id);
}
