package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.ProjectReadDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteDto;
import io.github.danielzyla.pdcaApp.service.ProjectService;
import io.jsonwebtoken.Jwts;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
class ProjectApiController {

    private final String secret;
    private final ProjectService projectService;

    ProjectApiController(ProjectService projectService) {
        this.projectService = projectService;
        this.secret = System.getenv("SECRET");
    }

    @PostMapping
    public ResponseEntity<ProjectReadDto> newProject(@RequestBody ProjectWriteApiDto apiDto) {
        ProjectWriteDto toSave = projectService.toProjectWriteDto(apiDto);
        ProjectReadDto result = projectService.createNewProject(toSave);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping
    public List<ProjectReadDto> readAll(HttpServletRequest request) {
        String token = request.getHeader("authorization").substring(7);
        String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        if (username.equals("admin")) {
            return projectService.getAll();
        } else {
            return projectService.getAllByUsername(username);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectReadDto> getProjectById(@PathVariable("id") Long id) {
        try {
            ProjectReadDto found = projectService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Void> updateProject(@RequestBody ProjectWriteApiDto apiDto) {
        try {
            ProjectWriteDto toSave = projectService.toProjectWriteDto(apiDto);
            projectService.updateProject(toSave);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeProject(@RequestParam("id") Long id) {
        try {
            projectService.removeProject(id);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
    }
}
