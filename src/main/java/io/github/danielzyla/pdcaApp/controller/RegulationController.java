package io.github.danielzyla.pdcaApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/regulations")
class RegulationController {

    @GetMapping
    public String showRegulations() {
        return "regulations";
    }

    @GetMapping("/policy")
    public String showPolicy() {
        return "policy";
    }

}
