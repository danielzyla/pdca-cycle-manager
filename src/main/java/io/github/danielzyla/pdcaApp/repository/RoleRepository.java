package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    Role save(Role entity);
}
