package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.model.User;
import io.github.danielzyla.pdcaApp.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        try {
            final Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                throw new UsernameNotFoundException(String.format("Nie znaleziono użytkownika o nazwie: %s", username));
            }

            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return userOptional.get().getRoles().stream().map(
                            role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet()
                    );
                }

                @Override
                public String getPassword() {
                    return userOptional.get().getPassword();
                }

                @Override
                public String getUsername() {
                    return userOptional.get().getUsername();
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return userOptional.get().isEnabled();
                }
            };
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
