package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.PlanPhase;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PlanPhaseReadDto {
    private final long id;
    private final LocalDateTime startTime;
    private final String name;
    private final String problemDescription;
    private final String currentSituationAnalysis;
    private final String goal;
    private final String rootCauseIdentification;
    private final String optimalSolutionChoice;
    private final List<Employee> employees;
    private final LocalDateTime endTime;
    private final boolean complete;

    public PlanPhaseReadDto(final PlanPhase source) {
        this.id = source.getId();
        this.startTime = source.getStartTime();
        this.name = source.getName();
        this.problemDescription = source.getProblemDescription();
        this.currentSituationAnalysis = source.getCurrentSituationAnalysis();
        this.goal = source.getGoal();
        this.rootCauseIdentification = source.getRootCauseIdentification();
        this.optimalSolutionChoice = source.getOptimalSolutionChoice();
        this.employees = new ArrayList<>(source.getEmployees());
        this.endTime = source.getEndTime();
        this.complete = source.isComplete();
    }
}
