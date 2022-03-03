package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.ActPhaseTaskWriteDto;
import io.github.danielzyla.pdcaApp.model.ActPhaseTask;
import io.github.danielzyla.pdcaApp.model.TaskStatus;
import io.github.danielzyla.pdcaApp.repository.ActPhaseTaskRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class ActPhaseTaskService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );
    private final ActPhaseTaskRepository actPhaseTaskRepository;

    public ActPhaseTaskService(final ActPhaseTaskRepository actPhaseTaskRepository) {
        this.actPhaseTaskRepository = actPhaseTaskRepository;
    }

    public ActPhaseTask create(final ActPhaseTaskWriteDto actPhaseTaskWriteDto) {
        actPhaseTaskWriteDto.setDescription("wpisz opis zadania ...");
        actPhaseTaskWriteDto.setStartTime(LocalDateTime.now());
        actPhaseTaskWriteDto.setTaskStatus(TaskStatus.OCZEKUJE);
        return actPhaseTaskRepository.save(actPhaseTaskWriteDto.toSave());
    }

    public ActPhaseTask getById(Long id) {
        Optional<ActPhaseTask> task = actPhaseTaskRepository.findById(id);

        if(task.isPresent()) {
            return task.get();
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateTask(Long id, final ActPhaseTaskWriteDto actPhaseTaskWriteDto) {
        Optional<ActPhaseTask> foundById = actPhaseTaskRepository.findById(id);

        if(foundById.isPresent()) {
            ActPhaseTask toSave = foundById.get();

            if(actPhaseTaskWriteDto.getDescription() == null || actPhaseTaskWriteDto.getDescription().equals("")) {
                toSave.setDescription(toSave.getDescription());
            } else {
                toSave.setDescription(actPhaseTaskWriteDto.getDescription());
            }

            if(actPhaseTaskWriteDto.getDeadline() == null) {
                toSave.setDeadline(toSave.getDeadline());
            } else {
                toSave.setDeadline(actPhaseTaskWriteDto.getDeadline());
            }

            if(actPhaseTaskWriteDto.getTaskStatus() == null) {
                toSave.setTaskStatus(toSave.getTaskStatus());
            } else {
                toSave.setTaskStatus(actPhaseTaskWriteDto.getTaskStatus());
                if(actPhaseTaskWriteDto.getTaskStatus() == TaskStatus.WYKONANE) {
                    toSave.setComplete(true);
                    toSave.setExecutionTime(LocalDateTime.now());
                } else {
                    toSave.setComplete(false);
                    toSave.setExecutionTime(null);
                }
            }

            if(toSave.getEmployees().containsAll(new HashSet<>(actPhaseTaskWriteDto.getEmployees()))) {
                toSave.getEmployees().removeAll(new HashSet<>(actPhaseTaskWriteDto.getEmployees()));
            } else {
                toSave.getEmployees().addAll(new HashSet<>(actPhaseTaskWriteDto.getEmployees()));
            }
            toSave.setEmployees(toSave.getEmployees());

            actPhaseTaskRepository.save(toSave);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void removeTask(Long id) {
        actPhaseTaskRepository.deleteById(id);
    }
}
