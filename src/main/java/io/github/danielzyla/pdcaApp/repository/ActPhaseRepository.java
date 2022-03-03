package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.ActPhase;

import java.util.Optional;

public interface ActPhaseRepository {
    ActPhase save(ActPhase entity);
    Optional<ActPhase> findById(Long id);
}
