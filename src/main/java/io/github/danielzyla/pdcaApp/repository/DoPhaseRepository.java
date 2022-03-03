package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.DoPhase;

import java.util.Optional;

public interface DoPhaseRepository {
    DoPhase save(DoPhase entity);
    Optional<DoPhase> findById(Long id);
}
