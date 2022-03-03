package io.github.danielzyla.pdcaApp;

import io.github.danielzyla.pdcaApp.filter.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class PdcaAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdcaAppApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> filterRegistrationBean() {
        FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtFilter(System.getenv("SECRET")));
        filterRegistrationBean.setUrlPatterns(Arrays.asList(
                "/api/projects",
                "/api/projects/*",
                "/api/departments",
                "/api/products",
                "/api/employees",
                "/api/plan_phases",
                "/api/plan_phases/*",
                "/api/do_phases",
                "/api/do_phases/*",
                "/api/check_phases",
                "/api/check_phases/*",
                "/api/act_phases",
                "/api/act_phases/*",
                "/api/tasks",
                "/api/cycles",
                "/api/do_phases/add_task",
                "/api/act_phases/add_task"
        ));
        return filterRegistrationBean;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(System.getenv("ADMIN_EMAIL"));
        mailSender.setPassword(System.getenv("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return mailSender;
    }
}
