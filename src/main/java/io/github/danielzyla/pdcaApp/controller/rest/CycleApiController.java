package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.service.CycleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@RequestMapping("/api/cycles")
class CycleApiController {

    private final CycleService cycleService;

    CycleApiController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @PostMapping
    ResponseEntity<Void> createNextCycle(@RequestBody Long projectId) {
        try {
            cycleService.nextCycle(projectId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
}
