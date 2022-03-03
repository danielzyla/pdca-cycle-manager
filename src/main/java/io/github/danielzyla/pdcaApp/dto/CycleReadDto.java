package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CycleReadDto {
    private final long id;
    private final String cycleName;
    private final LocalDateTime startTime;
    private final PlanPhase planPhase;
    private final DoPhase doPhase;
    private final CheckPhase checkPhase;
    private final ActPhase actPhase;
    private final LocalDateTime endTime;
    private final boolean complete;
    private final Project project;
    private final boolean nextCycle;

    public CycleReadDto(Cycle source) {
        this.id = source.getId();
        this.cycleName = source.getCycleName();
        this.startTime = source.getStartTime();
        this.planPhase = source.getPlanPhase();
        this.doPhase = source.getDoPhase();
        this.checkPhase = source.getCheckPhase();
        this.actPhase = source.getActPhase();
        this.endTime = source.getEndTime();
        this.complete = source.isComplete();
        this.project = source.getProject();
        this.nextCycle = source.isNextCycle();
    }
}
