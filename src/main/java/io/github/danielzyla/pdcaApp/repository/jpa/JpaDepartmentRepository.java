package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.repository.DepartmentRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface JpaDepartmentRepository extends DepartmentRepository, JpaRepository<Department, Integer> {
    @Override
    Department save(Department entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "employees",
                    "projects",
            }
    )
    List<Department> findAll();

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "employees",
                    "projects",
            }
    )
    List<Department> findByDeptNameContains(String phrase);

    @Override
    void deleteById(Integer id);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "employees",
                    "projects",
            }
    )
    Optional<Department> findById(Integer id);
}
