package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.repository.EmployeeRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface JpaEmployeeRepository extends EmployeeRepository, JpaRepository<Employee, Long> {
    @Override
    Employee save(Employee entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "planPhases",
                    "tasks",
                    "tasks.employees",
                    "department"
            }
    )
    List<Employee> findAll();

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "planPhases",
                    "tasks",
                    "tasks.employees",
                    "department"
            }
    )
    List<Employee> findByNameContainsOrSurnameContains(String name, String surName);

    @Override
    void deleteById(Long id);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "planPhases",
                    "tasks",
                    "tasks.employees",
                    "department"
            }
    )
    Optional<Employee> findById(Long id);
}
