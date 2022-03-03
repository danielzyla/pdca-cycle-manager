package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.ActPhase;
import io.github.danielzyla.pdcaApp.repository.ActPhaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaActPhaseRepository extends ActPhaseRepository, JpaRepository<ActPhase, Long> {
    @Override
    ActPhase save(ActPhase entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"actPhaseTasks", "actPhaseTasks.employees", "actPhaseTasks.employees.department"}
    )
    Optional<ActPhase> findById(Long id);
}
