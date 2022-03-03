package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.ActPhaseTask;

import java.util.Optional;

public interface ActPhaseTaskRepository {
    ActPhaseTask save(ActPhaseTask entity);
    Optional<ActPhaseTask> findById(Long id);
    void deleteById(Long id);
}
