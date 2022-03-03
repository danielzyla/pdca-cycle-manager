package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Task;
import io.github.danielzyla.pdcaApp.repository.TaskRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface JpaTaskRepository extends TaskRepository, JpaRepository<Task, Long> {
    @Override
    Task save(Task toSave);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"employees", "employees.department"}
    )
    Optional<Task> findById(Long id);

    @Override
    void deleteById(Long id);
}
