package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.PlanPhase;

import java.util.Optional;

public interface PlanPhaseRepository {
    PlanPhase save(PlanPhase entity);
    Optional<PlanPhase> findById(Long id);
}
