package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Role;
import io.github.danielzyla.pdcaApp.repository.RoleRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface JpaRoleRepository extends RoleRepository, JpaRepository<Role, Long> {
    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"users"}
    )
    Optional<Role> findByName(String name);

    @Override
    Role save(Role entity);
}
