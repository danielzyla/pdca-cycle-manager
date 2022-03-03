package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.User;

import java.util.Optional;

public interface UserRepository{
    User save(User entity);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByConfirmationToken(String token);
}
