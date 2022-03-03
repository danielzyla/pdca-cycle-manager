package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.TaskWriteDto;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.Task;
import io.github.danielzyla.pdcaApp.model.TaskStatus;
import io.github.danielzyla.pdcaApp.repository.TaskRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );

    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;

    public TaskService(TaskRepository taskRepository, EmployeeService employeeService) {
        this.taskRepository = taskRepository;
        this.employeeService = employeeService;
    }

    public void updateTask(final TaskWriteDto taskWriteDto) {
        Optional<Task> foundById = taskRepository.findById(taskWriteDto.getId());

        if(foundById.isPresent()) {
            Task toSave = foundById.get();

            if(taskWriteDto.getDescription() == null || taskWriteDto.getDescription().equals("")) {
                toSave.setDescription(toSave.getDescription());
            } else {
                toSave.setDescription(taskWriteDto.getDescription());
            }

            if(taskWriteDto.getDeadline() == null) {
                toSave.setDeadline(toSave.getDeadline());
            } else {
                toSave.setDeadline(taskWriteDto.getDeadline());
            }

            if(taskWriteDto.getTaskStatus() == null) {
                toSave.setTaskStatus(toSave.getTaskStatus());
            } else {
                toSave.setTaskStatus(taskWriteDto.getTaskStatus());
                if(taskWriteDto.getTaskStatus() == TaskStatus.WYKONANE) {
                    toSave.setComplete(true);
                    toSave.setExecutionTime(LocalDateTime.now());
                } else {
                    toSave.setComplete(false);
                    toSave.setExecutionTime(null);
                }
            }

            List<Employee> employeeList = new ArrayList<>();
            for(Long id : taskWriteDto.getEmployeeIds()) {
                employeeList.add(employeeService.getEmployeeById(id));
            }
            toSave.setEmployees(new HashSet<>(employeeList));
            taskRepository.save(toSave);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
