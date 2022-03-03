package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.DoPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.DoPhaseTaskWriteDto;
import io.github.danielzyla.pdcaApp.dto.DoPhaseWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.DoPhaseWriteDto;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.DoPhase;
import io.github.danielzyla.pdcaApp.model.DoPhaseTask;
import io.github.danielzyla.pdcaApp.repository.CheckPhaseRepository;
import io.github.danielzyla.pdcaApp.repository.CycleRepository;
import io.github.danielzyla.pdcaApp.repository.DoPhaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DoPhaseService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION =
            new ResourceNotFoundException("Nie istnieje rekord dla podanego id");
    private final DoPhaseRepository doPhaseRepository;
    private final DoPhaseTaskService doPhaseTaskService;
    private final CycleRepository cycleRepository;
    private final CheckPhaseRepository checkPhaseRepository;

    public DoPhaseReadDto getById(Long id) {
        Optional<DoPhase> optionalDoPhase = doPhaseRepository.findById(id);
        if (optionalDoPhase.isPresent()) {
            return new DoPhaseReadDto(optionalDoPhase.get());
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCycleDoPhase(final DoPhaseWriteDto update) {
        Optional<Cycle> foundById = cycleRepository.findById(update.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            DoPhase newData = update.toSave();
            DoPhase edited = toSave.getDoPhase();
            if(newData.getDescription() != null) {
                edited.setDescription(newData.getDescription());
            }
            if(newData.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.getCheckPhase().setStartTime(timeNow);
                checkPhaseRepository.save(toSave.getCheckPhase());
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
                edited.getDoPhaseTasks().addAll(newData.getDoPhaseTasks());
            }
            edited.setDoPhaseTasks(edited.getDoPhaseTasks());
            doPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void addTaskIntoDoPhase(final DoPhaseWriteDto update) {
        Optional<DoPhase> foundById = doPhaseRepository.findById(update.getId());

        if(foundById.isPresent()) {
            DoPhase newData = update.toSave();
            DoPhase edited = foundById.get();
            edited.getDoPhaseTasks().addAll(newData.getDoPhaseTasks());
            doPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCycleDoPhaseApi(DoPhaseWriteApiDto dto) {
        Optional<Cycle> foundById = cycleRepository.findById(dto.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            DoPhase edited = toSave.getDoPhase();
            if(dto.getDescription() != null) {
                edited.setDescription(dto.getDescription());
            }
            if(dto.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.getCheckPhase().setStartTime(timeNow);
                checkPhaseRepository.save(toSave.getCheckPhase());
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
            }
            doPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void addDoPhaseTask(Long id) {
        DoPhaseTask doPhaseTask = doPhaseTaskService.create(new DoPhaseTaskWriteDto());
        Optional<DoPhase> doPhaseOptional = doPhaseRepository.findById(id);
        if (doPhaseOptional.isPresent()) {
            DoPhase doPhase = doPhaseOptional.get();
            doPhase.getDoPhaseTasks().add(doPhaseTask);
            doPhaseRepository.save(doPhase);
        }
    }
}
