package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project entity);
    List<Project> findAll();
    List<Project> findAllByUsername(String username);
    void deleteById(Long id);
    Optional<Project> findById(Long id);
}
