package io.github.danielzyla.pdcaApp.dto;

import lombok.Getter;

@Getter
public class ActPhaseWriteApiDto {
    private long id;
    private String description;
    private boolean complete;
    private boolean nextCycle;
}
