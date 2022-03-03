package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.EmployeeReadDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteDto;
import io.github.danielzyla.pdcaApp.service.EmployeeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
class EmployeeApiController {

    private final EmployeeService employeeService;

    EmployeeApiController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeReadDto> newProject(@RequestBody EmployeeWriteApiDto apiDto) {
        EmployeeWriteDto toSave = employeeService.toEmployeeWriteDto(apiDto);
        EmployeeReadDto result = employeeService.create(toSave);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping
    public List<EmployeeReadDto> readAll() {
        return employeeService.getAll();
    }

    @PutMapping
    public ResponseEntity<Void> updateEmployee(@RequestBody EmployeeWriteApiDto apiDto) {
        try {
            EmployeeWriteDto toSave = employeeService.toEmployeeWriteDto(apiDto);
            employeeService.update(toSave);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>((HttpStatus.NO_CONTENT));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeEmployee(@RequestParam("id") Long id) {
        try {
            employeeService.removeEmployee(id);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
    }
}
