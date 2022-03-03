package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.DepartmentReadDto;
import io.github.danielzyla.pdcaApp.dto.DepartmentWriteDto;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.repository.DepartmentRepository;
import io.github.danielzyla.pdcaApp.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/departments")
class DepartmentApiController {
    DepartmentService departmentService;
    DepartmentRepository departmentRepository;

    @PostMapping
    public ResponseEntity<DepartmentReadDto> newDepartment(@RequestBody DepartmentWriteDto department) {
        DepartmentReadDto saved = departmentService.create(department);
        return ResponseEntity.created(URI.create("/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<DepartmentReadDto> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/id")
    public ResponseEntity<Department> getById(@PathVariable Integer id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        return optionalDepartment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Void> updateDepartment(@RequestBody DepartmentWriteDto departmentWriteDto) {
        try {
            departmentService.update(departmentWriteDto);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>((HttpStatus.NO_CONTENT));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeDepartment(@RequestParam("id") Integer id) {
        try {
            departmentService.removeDepartment(id);
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
