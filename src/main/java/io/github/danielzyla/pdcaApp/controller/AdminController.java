package io.github.danielzyla.pdcaApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
class AdminController {

    @RequestMapping
    public String showPanel() {
        return "manage";
    }
}
