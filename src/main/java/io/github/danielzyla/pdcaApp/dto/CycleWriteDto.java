package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
public class CycleWriteDto {
    @Setter(value = AccessLevel.NONE)
    private long id;
    private String cycleName;
    private LocalDateTime startTime;
    private PlanPhase planPhase = new PlanPhase();
    private DoPhase doPhase = new DoPhase();
    private CheckPhase checkPhase = new CheckPhase();
    private ActPhase actPhase = new ActPhase();
    private LocalDateTime endTime;
    private boolean complete;
    private Project project;

    public Cycle toSave() {
        var cycle = new Cycle();
        cycle.setCycleName(cycleName);
        cycle.setStartTime(startTime);
        cycle.setPlanPhase(planPhase);
        cycle.setDoPhase(doPhase);
        cycle.setCheckPhase(checkPhase);
        cycle.setActPhase(actPhase);
        cycle.setEndTime(endTime);
        cycle.setComplete(complete);
        cycle.setProject(project);
        return cycle;
    }
}
