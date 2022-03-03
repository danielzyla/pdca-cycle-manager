package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.User;
import io.github.danielzyla.pdcaApp.repository.UserRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface JpaUserRepository extends UserRepository, JpaRepository<User, Long> {

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"roles"}
    )
    User save(User entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"roles"}
    )
    Optional<User> findByUsername(String username);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"roles"}
    )
    Optional<User> findByEmail(String email);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"roles"}
    )
    Optional<User> findByConfirmationToken(String token);
}
