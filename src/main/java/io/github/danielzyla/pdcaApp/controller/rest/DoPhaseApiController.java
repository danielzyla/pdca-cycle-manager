package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.DoPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.DoPhaseWriteApiDto;
import io.github.danielzyla.pdcaApp.service.DoPhaseService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@RequestMapping("/api/do_phases")
class DoPhaseApiController {

    private final DoPhaseService doPhaseService;

    DoPhaseApiController(DoPhaseService doPhaseService) {
        this.doPhaseService = doPhaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoPhaseReadDto> getDoPhaseById(@PathVariable("id") Long id) {
        try {
            DoPhaseReadDto found = doPhaseService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Void> updateDoPhase(@RequestBody DoPhaseWriteApiDto toSave) {
        try {
            doPhaseService.updateCycleDoPhaseApi(toSave);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/add_task")
    public ResponseEntity<Void> addDoPhaseTask(@RequestBody Long id) {
        try {
            doPhaseService.addDoPhaseTask(id);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
