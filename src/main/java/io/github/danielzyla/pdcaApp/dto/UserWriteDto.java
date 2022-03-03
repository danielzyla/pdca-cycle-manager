package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Role;
import io.github.danielzyla.pdcaApp.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
public class UserWriteDto {
    @NotBlank(message = "pole nie może być puste")
    private String username, password;
    @NotBlank(message = "pole nie może być puste")
    @Email(message = "niepoprawny format")
    private String email;
    private Set<Role> roles = new HashSet<>();
    private boolean enabled;
    private String confirmationToken;

    public User toSave() {
        var user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRoles(roles);
        user.setEnabled(enabled);
        user.setConfirmationToken(confirmationToken);
        return user;
    }
}
