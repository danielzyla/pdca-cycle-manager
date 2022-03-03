package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.PlanPhase;
import io.github.danielzyla.pdcaApp.repository.PlanPhaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaPlanPhaseRepository extends PlanPhaseRepository, JpaRepository<PlanPhase, Long> {
    @Override
    PlanPhase save(PlanPhase entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"employees", "employees.department"}
    )
    Optional<PlanPhase> findById(Long id);
}
