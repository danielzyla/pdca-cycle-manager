package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.PlanPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.PlanPhaseWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.PlanPhaseWriteDto;
import io.github.danielzyla.pdcaApp.service.PlanPhaseService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@RequestMapping("/api/plan_phases")
class PlanPhaseApiController {

    private final PlanPhaseService planPhaseService;

    PlanPhaseApiController(PlanPhaseService planPhaseService) {
        this.planPhaseService = planPhaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanPhaseReadDto> getPlanPhaseById(@PathVariable("id") Long id) {
        try {
            PlanPhaseReadDto found = planPhaseService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Void> updatePlanPhase(@RequestBody PlanPhaseWriteApiDto apiDto) {
        try {
            PlanPhaseWriteDto toSave = planPhaseService.toPlanPhaseWriteDto(apiDto);
            planPhaseService.updateCyclePlanApi(toSave);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
