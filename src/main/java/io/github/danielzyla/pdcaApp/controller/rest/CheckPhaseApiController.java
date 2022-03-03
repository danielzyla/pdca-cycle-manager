package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.CheckPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.CheckPhaseWriteDto;
import io.github.danielzyla.pdcaApp.service.CheckPhaseService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@RequestMapping("/api/check_phases")
class CheckPhaseApiController {

    private final CheckPhaseService checkPhaseService;

    CheckPhaseApiController(CheckPhaseService checkPhaseService) {
        this.checkPhaseService = checkPhaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckPhaseReadDto> getCheckPhaseById(@PathVariable("id") Long id) {
        try {
            CheckPhaseReadDto found = checkPhaseService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Void> updateCheckPhase(@RequestBody CheckPhaseWriteDto toSave) {
        try {
            checkPhaseService.updateCycleCheckPhase(toSave);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
