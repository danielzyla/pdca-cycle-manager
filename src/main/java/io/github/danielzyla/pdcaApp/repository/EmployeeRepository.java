package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee entity);
    List<Employee> findAll();
    List<Employee> findByNameContainsOrSurnameContains(String name, String surName);
    void deleteById(Long id);
    Optional<Employee> findById(Long id);
}
