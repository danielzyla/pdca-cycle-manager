package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.UserWriteDto;
import io.github.danielzyla.pdcaApp.security.CustomAuthenticationProvider;
import io.github.danielzyla.pdcaApp.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
class AuthenticationController {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final String secret;
    long expirationTimeInMs;

    AuthenticationController(
            final CustomUserDetailsService customUserDetailsService,
            final CustomAuthenticationProvider customAuthenticationProvider
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.secret = System.getenv("SECRET");
        this.expirationTimeInMs = Long.parseLong(System.getenv("JWT_EXPIRATION"));
    }

    @PostMapping(value = "/api/login")
    public ResponseEntity<String> authenticateRestClient(@RequestBody UserWriteDto user) throws Exception {
        try {
            customAuthenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        Date now = new Date(System.currentTimeMillis());
        return ResponseEntity.ok(
                Jwts.builder()
                        .setSubject(userDetails.getUsername())
                        .claim("roles", userDetails.getAuthorities()).setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + expirationTimeInMs))
                        .signWith(SignatureAlgorithm.HS512, secret)
                        .compact()
        );
    }
}
