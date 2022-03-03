package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.User;
import lombok.Getter;

@Getter
public class UserReadDto {
    private final String username;
    private final String email;

    public UserReadDto(User source) {
        this.username = source.getUsername();
        this.email = source.getEmail();
    }
}
