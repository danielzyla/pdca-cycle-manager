package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.DoPhaseTaskWriteDto;
import io.github.danielzyla.pdcaApp.model.DoPhaseTask;
import io.github.danielzyla.pdcaApp.model.TaskStatus;
import io.github.danielzyla.pdcaApp.repository.DoPhaseTaskRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class DoPhaseTaskService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord o podanym id"
    );
    private final DoPhaseTaskRepository doPhaseTaskRepository;

    public DoPhaseTaskService(final DoPhaseTaskRepository doPhaseTaskRepository) {
        this.doPhaseTaskRepository = doPhaseTaskRepository;
    }

    public DoPhaseTask create(final DoPhaseTaskWriteDto doPhaseTaskWriteDto) {
        doPhaseTaskWriteDto.setDescription("wpisz opis zadania ...");
        doPhaseTaskWriteDto.setStartTime(LocalDateTime.now());
        doPhaseTaskWriteDto.setTaskStatus(TaskStatus.OCZEKUJE);
        return doPhaseTaskRepository.save(doPhaseTaskWriteDto.toSave());
    }

    public DoPhaseTask getById(Long id) {
        Optional<DoPhaseTask> task = doPhaseTaskRepository.findById(id);

        if(task.isPresent()) {
            return task.get();
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateTask(Long id, final DoPhaseTaskWriteDto doPhaseTaskWriteDto) {
        Optional<DoPhaseTask> foundById = doPhaseTaskRepository.findById(id);

        if(foundById.isPresent()) {
            DoPhaseTask toSave = foundById.get();

            if(doPhaseTaskWriteDto.getDescription() == null || doPhaseTaskWriteDto.getDescription().equals("")) {
                toSave.setDescription(toSave.getDescription());
            } else {
                toSave.setDescription(doPhaseTaskWriteDto.getDescription());
            }
            if(doPhaseTaskWriteDto.getDeadline() == null) {
                toSave.setDeadline(toSave.getDeadline());
            } else {
                toSave.setDeadline(doPhaseTaskWriteDto.getDeadline());
            }
            if(doPhaseTaskWriteDto.getTaskStatus() == null) {
                toSave.setTaskStatus(toSave.getTaskStatus());
            } else {
                toSave.setTaskStatus(doPhaseTaskWriteDto.getTaskStatus());
                if(doPhaseTaskWriteDto.getTaskStatus() == TaskStatus.WYKONANE) {
                    toSave.setComplete(true);
                    toSave.setExecutionTime(LocalDateTime.now());
                } else {
                    toSave.setComplete(false);
                    toSave.setExecutionTime(null);
                }
            }
            if(toSave.getEmployees().containsAll(new HashSet<>(doPhaseTaskWriteDto.getEmployees()))) {
                toSave.getEmployees().removeAll(new HashSet<>(doPhaseTaskWriteDto.getEmployees()));
            } else {
                toSave.getEmployees().addAll(new HashSet<>(doPhaseTaskWriteDto.getEmployees()));
            }
            toSave.setEmployees(toSave.getEmployees());
            doPhaseTaskRepository.save(toSave);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void removeTask(Long id) {
        doPhaseTaskRepository.deleteById(id);
    }
}
