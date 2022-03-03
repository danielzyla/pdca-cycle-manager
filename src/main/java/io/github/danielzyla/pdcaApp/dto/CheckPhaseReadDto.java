package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.CheckPhase;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CheckPhaseReadDto {
    private final long id;
    private final LocalDateTime startTime;
    private final String conclusions;
    private final String achievements;
    private final String nextSteps;
    private final boolean complete;
    private final LocalDateTime endTime;

    public CheckPhaseReadDto(CheckPhase source) {
        this.id = source.getId();
        this.startTime = source.getStartTime();
        this.conclusions = source.getConclusions();
        this.achievements = source.getAchievements();
        this.nextSteps = source.getNextSteps();
        this.complete = source.isComplete();
        this.endTime = source.getEndTime();
    }
}
