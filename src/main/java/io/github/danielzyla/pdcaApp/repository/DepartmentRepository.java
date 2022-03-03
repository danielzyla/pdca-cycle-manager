package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {
    Department save(Department entity);
    List<Department> findAll();
    List<Department> findByDeptNameContains(String phrase);
    void deleteById(Integer id);
    Optional<Department> findById(Integer id);
}
