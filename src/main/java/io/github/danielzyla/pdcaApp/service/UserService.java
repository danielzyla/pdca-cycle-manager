package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUser(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
