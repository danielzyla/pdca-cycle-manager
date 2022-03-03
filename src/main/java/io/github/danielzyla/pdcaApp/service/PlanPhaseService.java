package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.PlanPhaseReadDto;
import io.github.danielzyla.pdcaApp.dto.PlanPhaseWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.PlanPhaseWriteDto;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.PlanPhase;
import io.github.danielzyla.pdcaApp.repository.CycleRepository;
import io.github.danielzyla.pdcaApp.repository.DoPhaseRepository;
import io.github.danielzyla.pdcaApp.repository.PlanPhaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PlanPhaseService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord o podanym id"
    );
    private final PlanPhaseRepository planPhaseRepository;
    private final CycleRepository cycleRepository;
    private final DoPhaseRepository doPhaseRepository;
    private final EmployeeService employeeService;

    public PlanPhaseReadDto getById(Long id) {
        Optional<PlanPhase> optionalPlanPhase = planPhaseRepository.findById(id);
        if (optionalPlanPhase.isPresent()) {
            return new PlanPhaseReadDto(optionalPlanPhase.get());
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCyclePlan(final PlanPhaseWriteDto update) {
        Optional<Cycle> foundById = cycleRepository.findById(update.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            PlanPhase newData = update.toSave();
            PlanPhase edited = toSave.getPlanPhase();

            if(newData.getProblemDescription() != null) {
                edited.setProblemDescription(newData.getProblemDescription());
            }
            if(newData.getCurrentSituationAnalysis() != null) {
                edited.setCurrentSituationAnalysis(newData.getCurrentSituationAnalysis());
            }
            if(newData.getGoal() != null) {
                edited.setGoal(newData.getGoal());
            }
            if(newData.getRootCauseIdentification() != null) {
                edited.setRootCauseIdentification(newData.getRootCauseIdentification());
            }
            if(newData.getOptimalSolutionChoice() != null) {
                edited.setOptimalSolutionChoice(newData.getOptimalSolutionChoice());
            }
            if(edited.getEmployees().containsAll(new HashSet<>(newData.getEmployees()))) {
                edited.getEmployees().removeAll(new HashSet<>(newData.getEmployees()));
            } else {
                edited.getEmployees().addAll(new HashSet<>(newData.getEmployees()));
            }
            edited.setEmployees(edited.getEmployees());
            if(newData.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.getDoPhase().setStartTime(timeNow);
                doPhaseRepository.save(toSave.getDoPhase());
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
            }
            planPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateCyclePlanApi(final PlanPhaseWriteDto update) {
        Optional<Cycle> foundById = cycleRepository.findById(update.getId());

        if(foundById.isPresent()) {
            Cycle toSave = foundById.get();
            PlanPhase newData = update.toSave();
            PlanPhase edited = toSave.getPlanPhase();

            if(newData.getProblemDescription() != null) {
                edited.setProblemDescription(newData.getProblemDescription());
            }
            if(newData.getCurrentSituationAnalysis() != null) {
                edited.setCurrentSituationAnalysis(newData.getCurrentSituationAnalysis());
            }
            if(newData.getGoal() != null) {
                edited.setGoal(newData.getGoal());
            }
            if(newData.getRootCauseIdentification() != null) {
                edited.setRootCauseIdentification(newData.getRootCauseIdentification());
            }
            if(newData.getOptimalSolutionChoice() != null) {
                edited.setOptimalSolutionChoice(newData.getOptimalSolutionChoice());
            }
            edited.setEmployees(new HashSet<>(newData.getEmployees()));
            if(newData.isComplete()) {
                edited.setComplete(true);
                LocalDateTime timeNow = LocalDateTime.now();
                edited.setEndTime(timeNow);
                toSave.getDoPhase().setStartTime(timeNow);
                doPhaseRepository.save(toSave.getDoPhase());
            } else {
                edited.setComplete(false);
                edited.setEndTime(null);
            }
            planPhaseRepository.save(edited);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public PlanPhaseWriteDto toPlanPhaseWriteDto(PlanPhaseWriteApiDto apiDto) {
        var planPhaseWriteDto = new PlanPhaseWriteDto();
        planPhaseWriteDto.setId(apiDto.getId());
        planPhaseWriteDto.setProblemDescription(apiDto.getProblemDescription());
        planPhaseWriteDto.setCurrentSituationAnalysis(apiDto.getCurrentSituationAnalysis());
        planPhaseWriteDto.setGoal(apiDto.getGoal());
        planPhaseWriteDto.setRootCauseIdentification(apiDto.getRootCauseIdentification());
        planPhaseWriteDto.setOptimalSolutionChoice(apiDto.getOptimalSolutionChoice());
        planPhaseWriteDto.setComplete(apiDto.isComplete());
        List<Employee> employeeList = new ArrayList<>();
        for(Long id : apiDto.getEmployeeIds()) {
            employeeList.add(employeeService.getEmployeeById(id));
        }
        planPhaseWriteDto.setEmployees(employeeList);
        return planPhaseWriteDto;
    }
}
