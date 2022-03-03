package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.DoPhase;
import io.github.danielzyla.pdcaApp.model.DoPhaseTask;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DoPhaseReadDto {
    private final long id;
    private final LocalDateTime startTime;
    private final String description;
    private final List<DoPhaseTask> doPhaseTasks;
    private final boolean complete;
    private final LocalDateTime endTime;

    public DoPhaseReadDto(DoPhase source) {
        this.id = source.getId();
        this.startTime = source.getStartTime();
        this.description = source.getDescription();
        this.doPhaseTasks = new ArrayList<>(source.getDoPhaseTasks());
        this.complete = source.isComplete();
        this.endTime = source.getEndTime();
    }
}
