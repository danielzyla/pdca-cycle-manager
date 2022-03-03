package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.DoPhaseTask;
import io.github.danielzyla.pdcaApp.repository.DoPhaseTaskRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaDoPhaseTaskRepository extends DoPhaseTaskRepository, JpaRepository<DoPhaseTask, Long> {
    @Override
    DoPhaseTask save(DoPhaseTask entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"employees", "employees.department"}
    )
    Optional<DoPhaseTask> findById(Long id);

    @Override
    void deleteById(Long id);
}
