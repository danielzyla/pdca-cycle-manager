package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Task;

import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findById(Long id);
    Task save(Task toSave);
    void deleteById(Long id);
}
