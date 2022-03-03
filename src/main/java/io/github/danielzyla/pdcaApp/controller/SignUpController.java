package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.UserWriteDto;
import io.github.danielzyla.pdcaApp.service.SignUpService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@AllArgsConstructor
@Controller
class SignUpController {

    private final SignUpService signUpService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    @GetMapping("/signUp")
    public String showSignUpPanel(Model model) {
        model.addAttribute("userToSave", new UserWriteDto());
        return "signUp";
    }

    @PostMapping("/signUp")
    public String signUpPanel(
            Model model,
            @Valid @ModelAttribute("userToSave") UserWriteDto toSave,
            BindingResult bindingResult
    ) throws DataIntegrityViolationException, ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return "signUp";
        }
        try {
            toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
            signUpService.saveNewUser(toSave);
            logger.info("SignUpController: saveNewUser method called");
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error_message", "istnieje już taki użytkownik, sprawdź email lub hasło");
            return "signUp";
        }

        return "redirect:/login";
    }

    @RequestMapping("/confirm_email")
    public String confirmEmail(
            Model model,
            @RequestParam(name = "token") String token
    ) throws ResourceNotFoundException {
        try {
            signUpService.confirm(token);
            logger.info("SignUpController: saveNewUser method called");
        } catch (Exception e) {
            logger.error("SignUpController: Exception in confirm_email" + e.getMessage());
            model.addAttribute("message", Arrays.toString(e.getStackTrace()) + " " + e.getCause() + " " + e.getMessage());
            return "confirm_email";
        }

        model.addAttribute("message", "użytkownik pomyślnie aktywowany");
        return "confirm_email";
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
