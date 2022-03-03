package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.*;
import io.github.danielzyla.pdcaApp.model.*;
import io.github.danielzyla.pdcaApp.repository.ActPhaseRepository;
import io.github.danielzyla.pdcaApp.repository.CycleRepository;
import io.github.danielzyla.pdcaApp.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ActPhaseService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );
    private final ActPhaseRepository actPhaseRepository;
    private final CycleRepository cycleRepository;
    private final ProjectRepository projectRepository;
    private final ActPhaseTaskService actPhaseTaskService;

    public ActPhaseReadDto getById(Long id) {
        Optional<ActPhase> optionalActPhase = actPhaseRepository.findById(id);
        if (optionalActPhase.isPresent()) {
            return new ActPhaseReadDto(optionalActPhase.get());
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCycleActPhase(final ActPhaseWriteDto update) {
        Optional<Cycle> foundById = cycleRepository.findById(update.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            ActPhase newData = update.toSave();
            ActPhase edited = toSave.getActPhase();

            if(newData.getDescription() != null) {
                edited.setDescription(newData.getDescription());
            }
            if(newData.isNextCycle() && !newData.isComplete()) {
                edited.setNextCycle(false);
            }
            if(newData.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.setComplete(true);
                toSave.setEndTime(timeNow);
                if(!newData.isNextCycle()) {
                    toSave.getProject().setComplete(true);
                    toSave.getProject().setEndTime(timeNow);
                    cycleRepository.save(toSave);
                    projectRepository.save(toSave.getProject());
                } else {
                    edited.setNextCycle(newData.isNextCycle());
                    toSave.setNextCycle(true);
                    cycleRepository.save(toSave);
                }
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
                actPhaseRepository.save(edited);
            }
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void addTaskIntoActPhase(final ActPhaseWriteDto update) {
        Optional<ActPhase> foundById = actPhaseRepository.findById(update.getId());

        if(foundById.isPresent()) {
            ActPhase newData = update.toSave();
            ActPhase edited = foundById.get();
            edited.getActPhaseTasks().addAll(newData.getActPhaseTasks());
            actPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCycleActPhaseApi(ActPhaseWriteApiDto dto) {
        Optional<Cycle> foundById = cycleRepository.findById(dto.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            ActPhase edited = toSave.getActPhase();

            if(dto.getDescription() != null) {
                edited.setDescription(dto.getDescription());
            }
            if(dto.isNextCycle() && !dto.isComplete()) {
                edited.setNextCycle(false);
            }
            if(dto.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.setComplete(true);
                toSave.setEndTime(timeNow);
                if(!dto.isNextCycle()) {
                    toSave.getProject().setComplete(true);
                    toSave.getProject().setEndTime(timeNow);
                    cycleRepository.save(toSave);
                    projectRepository.save(toSave.getProject());
                } else {
                    edited.setNextCycle(dto.isNextCycle());
                    toSave.setNextCycle(true);
                    cycleRepository.save(toSave);
                }
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
                actPhaseRepository.save(edited);
            }
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void addActPhaseTask(Long id) {
        ActPhaseTask actPhaseTask = actPhaseTaskService.create(new ActPhaseTaskWriteDto());
        Optional<ActPhase> actPhaseOptional = actPhaseRepository.findById(id);
        if (actPhaseOptional.isPresent()) {
            ActPhase actPhase = actPhaseOptional.get();
            actPhase.getActPhaseTasks().add(actPhaseTask);
            actPhaseRepository.save(actPhase);
        }
    }
}
