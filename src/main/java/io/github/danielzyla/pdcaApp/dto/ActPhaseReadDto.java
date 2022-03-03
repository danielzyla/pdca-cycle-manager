package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.ActPhase;
import io.github.danielzyla.pdcaApp.model.ActPhaseTask;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ActPhaseReadDto {
    private final long id;
    private final LocalDateTime startTime;
    private final String description;
    private final List<ActPhaseTask> actPhaseTasks;
    private final boolean complete;
    private final boolean nextCycle;
    private final LocalDateTime endTime;

    public ActPhaseReadDto(ActPhase source) {
        this.id = source.getId();
        this.startTime = source.getStartTime();
        this.description = source.getDescription();
        this.actPhaseTasks = new ArrayList<>(source.getActPhaseTasks());
        this.complete = source.isComplete();
        this.nextCycle = source.isNextCycle();
        this.endTime = source.getEndTime();
    }
}
