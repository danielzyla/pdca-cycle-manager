package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.ActPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.ActPhaseWriteApiDto;
import io.github.danielzyla.pdcaApp.service.ActPhaseService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@RequestMapping("/api/act_phases")
class ActPhaseApiController {

    private final ActPhaseService actPhaseService;

    ActPhaseApiController(ActPhaseService actPhaseService) {
        this.actPhaseService = actPhaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActPhaseReadDto> getActPhaseById(@PathVariable("id") Long id) {
        try {
            ActPhaseReadDto found = actPhaseService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Void> updateActPhase(@RequestBody ActPhaseWriteApiDto toSave) {
        try {
            actPhaseService.updateCycleActPhaseApi(toSave);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/add_task")
    public ResponseEntity<Void> addActPhaseTask(@RequestBody Long id) {
        try {
            actPhaseService.addActPhaseTask(id);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
