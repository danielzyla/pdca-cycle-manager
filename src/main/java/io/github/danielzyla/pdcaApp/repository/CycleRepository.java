package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Cycle;

import java.util.List;
import java.util.Optional;

public interface CycleRepository {
    Cycle save(Cycle entity);
    List<Cycle> findAll();
    Optional<Cycle> findById(Long id);
}
