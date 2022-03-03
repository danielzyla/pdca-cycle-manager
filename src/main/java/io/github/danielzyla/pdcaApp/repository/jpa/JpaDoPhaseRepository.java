package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.DoPhase;
import io.github.danielzyla.pdcaApp.repository.DoPhaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaDoPhaseRepository extends DoPhaseRepository, JpaRepository<DoPhase, Long> {
    @Override
    DoPhase save(DoPhase entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"doPhaseTasks", "doPhaseTasks.employees", "doPhaseTasks.employees.department"}
    )
    Optional<DoPhase> findById(Long id);
}
