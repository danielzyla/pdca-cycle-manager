package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.DoPhaseTask;

import java.util.Optional;

public interface DoPhaseTaskRepository {
    DoPhaseTask save(DoPhaseTask entity);
    Optional<DoPhaseTask> findById(Long id);
    void deleteById(Long id);
}
