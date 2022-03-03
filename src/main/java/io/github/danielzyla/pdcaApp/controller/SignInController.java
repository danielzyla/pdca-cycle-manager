package io.github.danielzyla.pdcaApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
class SignInController {

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name = "error", defaultValue = "") String errorMessage) {
        model.addAttribute("error", URLDecoder.decode(errorMessage, StandardCharsets.UTF_8));
        return "login";
    }
}
