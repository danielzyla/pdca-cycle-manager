package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.ActPhaseTask;
import io.github.danielzyla.pdcaApp.repository.ActPhaseTaskRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaActPhaseTaskRepository extends ActPhaseTaskRepository, JpaRepository<ActPhaseTask, Long> {
    @Override
    ActPhaseTask save(ActPhaseTask entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"employees", "employees.department"}
    )
    Optional<ActPhaseTask> findById(Long id);

    @Override
    void deleteById(Long id);
}
