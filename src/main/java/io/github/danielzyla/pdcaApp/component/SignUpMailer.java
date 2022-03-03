package io.github.danielzyla.pdcaApp.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@PropertySource(value = "classpath:email.properties", encoding = "UTF-8")
@Component
public class SignUpMailer {

    @Autowired
    JavaMailSender mailSender;
    @Value("${email.confirmation.subject}")
    String subject;
    @Value("${email.confirmation.text}")
    String text;
    @Value("${email.confirmation.link}")
    String link;

    public void sendConfirmationLink(String email, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(text + "\n" + link + token);
        mailSender.send(mailMessage);
    }
}
