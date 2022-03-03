package io.github.danielzyla.pdcaApp;

import io.github.danielzyla.pdcaApp.dto.UserWriteDto;
import io.github.danielzyla.pdcaApp.model.Role;
import io.github.danielzyla.pdcaApp.service.RoleService;
import io.github.danielzyla.pdcaApp.service.SignUpService;
import io.github.danielzyla.pdcaApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class Starter {

    private RoleService roleService;
    private UserService userService;
    private SignUpService signUpService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        if (!roleService.isRole("ADMIN")) {
            Role userRole = new Role();
            userRole.setName("ADMIN");
            roleService.createRole(userRole);
        }

        if (!roleService.isRole("USER")) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleService.createRole(userRole);
        }

        if (!userService.isUser("admin")) {
            UserWriteDto user = new UserWriteDto();
            user.setUsername("admin");
            user.setPassword(System.getenv("SECRET"));
            user.setEmail(System.getenv("ADMIN_EMAIL"));
            signUpService.saveAdminUser(user);
        }
    }
}
