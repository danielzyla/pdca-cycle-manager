package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Project;
import io.github.danielzyla.pdcaApp.repository.ProjectRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface JpaProjectRepository extends ProjectRepository, JpaRepository<Project, Long> {
    @Override
    Project save(Project entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "departments",
                    "products",
                    "cycles"
            }
    )
    List<Project> findAll();

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "departments",
                    "products",
                    "cycles"
            }
    )
    List<Project> findAllByUsername(String username);

    @Override
    void deleteById(Long id);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "departments",
                    "products",
                    "cycles",
                    "cycles.actPhase.nextCycle"
            }
    )
    Optional<Project> findById(Long id);
}
