package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.repository.CycleRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaCycleRepository extends CycleRepository, JpaRepository<Cycle, Long> {
    @Override
    Cycle save(Cycle entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.LOAD,
            attributePaths = {
                    "project",
                    "project.cycles",
                    "planPhase.employees",
                    "planPhase.employees.department",
                    "doPhase.complete",
                    "doPhase.doPhaseTasks",
                    "doPhase.doPhaseTasks.employees",
                    "doPhase.doPhaseTasks.employees.department",
                    "checkPhase.complete",
                    "actPhase.complete",
                    "actPhase.actPhaseTasks",
                    "actPhase.actPhaseTasks.employees",
                    "actPhase.actPhaseTasks.employees.department"
            }
    )
    Optional<Cycle> findById(Long id);

}
