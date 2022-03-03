package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.PlanPhase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class PlanPhaseWriteDto {
    private Long id;
    private String problemDescription,
            currentSituationAnalysis,
            goal,
            rootCauseIdentification,
            optimalSolutionChoice;
    private List<Employee> employees = new ArrayList<>();
    private boolean complete;

    public PlanPhase toSave() {
        var plan = new PlanPhase();
        plan.setProblemDescription(problemDescription);
        plan.setCurrentSituationAnalysis(currentSituationAnalysis);
        plan.setGoal(goal);
        plan.setRootCauseIdentification(rootCauseIdentification);
        plan.setOptimalSolutionChoice(optimalSolutionChoice);
        plan.setEmployees(new HashSet<>(employees));
        plan.setComplete(complete);
        return plan;
    }
}
