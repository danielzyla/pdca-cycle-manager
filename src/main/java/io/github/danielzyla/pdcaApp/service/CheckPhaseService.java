package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.CheckPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.CheckPhaseWriteDto;
import io.github.danielzyla.pdcaApp.model.CheckPhase;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.repository.ActPhaseRepository;
import io.github.danielzyla.pdcaApp.repository.CheckPhaseRepository;
import io.github.danielzyla.pdcaApp.repository.CycleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CheckPhaseService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );
    private final CheckPhaseRepository checkPhaseRepository;
    private final CycleRepository cycleRepository;
    private final ActPhaseRepository actPhaseRepository;


    public CheckPhaseReadDto getById(Long id) {
        Optional<CheckPhase> optionalCheckPhase = checkPhaseRepository.findById(id);
        if (optionalCheckPhase.isPresent()) {
            return new CheckPhaseReadDto(optionalCheckPhase.get());
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCycleCheckPhase(final CheckPhaseWriteDto update) {
        Optional<Cycle> foundById = cycleRepository.findById(update.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            CheckPhase newData = update.toSave();
            CheckPhase edited = toSave.getCheckPhase();

            if(newData.getConclusions() != null) {
                edited.setConclusions(newData.getConclusions());
            }
            if(newData.getAchievements() != null) {
                edited.setAchievements(newData.getAchievements());
            }
            if(newData.getNextSteps() != null) {
                edited.setNextSteps(newData.getNextSteps());
            }
            if(newData.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.getActPhase().setStartTime(timeNow);
                actPhaseRepository.save(toSave.getActPhase());
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
            }
            checkPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }
}
