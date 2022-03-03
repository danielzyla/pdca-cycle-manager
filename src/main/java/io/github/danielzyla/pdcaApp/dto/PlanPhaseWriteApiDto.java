package io.github.danielzyla.pdcaApp.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PlanPhaseWriteApiDto {
    private long id;
    private String problemDescription,
            currentSituationAnalysis,
            goal,
            rootCauseIdentification,
            optimalSolutionChoice;
    private List<Long> employeeIds;
    private boolean complete;
}
