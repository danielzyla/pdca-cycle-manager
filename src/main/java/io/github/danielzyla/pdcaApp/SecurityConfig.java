package io.github.danielzyla.pdcaApp;

import io.github.danielzyla.pdcaApp.security.CustomAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().ignoringAntMatchers("/api/**", "/h2console/**")
                .and().headers().frameOptions().disable()
                .and().authorizeRequests()
                .antMatchers(
                        "/",
                        "/css/**",
                        "/img/**",
                        "/api/**",
                        "/login",
                        "/login?error=**",
                        "/signUp",
                        "/regulations",
                        "/regulations/policy",
                        "/error",
                        "/confirm_email",
                        "/h2console/**"
                ).permitAll()
                .antMatchers(
                        "/projects/*",
                        "/manage",
                        "/products/editProduct/**",
                        "/products/*",
                        "/departments/editDepartment/**",
                        "/departments/*",
                        "/employees/editEmployee/**",
                        "/employees/*"
                ).hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").failureHandler(authenticationFailureHandler())
                .and().logout().deleteCookies("cookies").logoutSuccessUrl("/");
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}
