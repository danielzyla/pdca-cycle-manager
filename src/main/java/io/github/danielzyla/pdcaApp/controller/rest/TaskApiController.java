package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.TaskWriteDto;
import io.github.danielzyla.pdcaApp.service.TaskService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@RequestMapping("/api/tasks")
class TaskApiController {

    private final TaskService taskService;

    TaskApiController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PutMapping
    public ResponseEntity<Void> updateTask(@RequestBody TaskWriteDto taskWriteDto) {
        try {
            taskService.updateTask(taskWriteDto);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>((HttpStatus.NO_CONTENT));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeTask(@RequestParam("id") Long id) {
        try {
            taskService.deleteTask(id);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
