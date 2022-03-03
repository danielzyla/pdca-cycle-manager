package io.github.danielzyla.pdcaApp.security;

import io.github.danielzyla.pdcaApp.model.User;
import io.github.danielzyla.pdcaApp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@AllArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final static Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("nieprawidłowa nazwa użytkownika lub hasło");
        }

        String username = authentication.getName();
        Assert.notNull(authentication.getName(), "username_cannot_be_null");

        Assert.notNull(authentication.getCredentials(), "credentials_cannot_be_null");
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("nieprawidłowe hasło");
        }

        if (userDetails.isEnabled()) {
            return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        } else {
            throw new AccountStatusException("konto nie zostało aktywowane, wysłano e-mail aktywacyjny") {
                @Override
                public String getMessage() {
                    logger.error("Login failed: activation of user '" + username + "' required");
                    return super.getMessage();
                }
            };
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
