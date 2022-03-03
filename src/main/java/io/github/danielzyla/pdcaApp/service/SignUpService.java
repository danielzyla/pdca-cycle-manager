package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.component.RandomStringFactory;
import io.github.danielzyla.pdcaApp.component.SignUpMailer;
import io.github.danielzyla.pdcaApp.dto.UserWriteDto;
import io.github.danielzyla.pdcaApp.model.Role;
import io.github.danielzyla.pdcaApp.model.User;
import io.github.danielzyla.pdcaApp.repository.RoleRepository;
import io.github.danielzyla.pdcaApp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class SignUpService {

    public static final int TOKEN_LENGTH = 60;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SignUpMailer signUpMailer;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(SignUpService.class);

    public void saveNewUser(UserWriteDto userDto){
        Optional<User> found = userRepository.findByEmail(userDto.getEmail());
        if(found.isPresent()) {
            throw new DataIntegrityViolationException("Istnieje już użytkownik dla podanego adresu e-mail");
        }

        Optional<Role> roleOptional = roleRepository.findByName("USER");
        if(roleOptional.isEmpty()) {
            throw new ResourceNotFoundException("nie można przypisać roli użytkownikowi, jeśli problem " +
                    "będzie się powtarzał skontaktuj się z administratorem");
        }
        userDto.getRoles().add(roleOptional.get());

        String token = RandomStringFactory.getRandomString(TOKEN_LENGTH);
        userDto.setConfirmationToken(token);

        userRepository.save(userDto.toSave());
        signUpMailer.sendConfirmationLink(userDto.getEmail(), token);
    }

    public void saveAdminUser(UserWriteDto userDto){
        Optional<Role> roleOptional = roleRepository.findByName("ADMIN");
        if(roleOptional.isEmpty()) {
            throw new ResourceNotFoundException("nie można przypisać roli użytkownikowi, jeśli problem " +
                    "będzie się powtarzał skontaktuj się z administratorem");
        }
        userDto.getRoles().add(roleOptional.get());
        String token = RandomStringFactory.getRandomString(TOKEN_LENGTH);
        userDto.setConfirmationToken(token);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setEnabled(true);
        userRepository.save(userDto.toSave());
    }

    public void confirm(final String token) {
        Optional<User> userOptional = userRepository.findByConfirmationToken(token);
        logger.info("SignUpService: userOptional isPresent: " + userOptional.isPresent());
        if(userOptional.isPresent()) {
            User toActivate = userOptional.get();
            toActivate.setEnabled(true);
            userRepository.save(toActivate);
            logger.info("SignUpService : user activated");
        } else {
            logger.error("SignUpService: ResourceNotFoundException exception appeared !");
            throw new ResourceNotFoundException("nie znaleziono w bazie takiego użytkownika");
        }
    }
}
