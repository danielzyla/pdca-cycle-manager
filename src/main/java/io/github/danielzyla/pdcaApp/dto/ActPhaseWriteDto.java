package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.ActPhase;
import io.github.danielzyla.pdcaApp.model.ActPhaseTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ActPhaseWriteDto {
    private long id;
    private String description;
    private List<ActPhaseTask> actPhaseTasks = new ArrayList<>();
    private boolean complete;
    private boolean nextCycle;

    public ActPhase toSave() {
        var actPhase = new ActPhase();
        actPhase.setDescription(description);
        actPhase.setActPhaseTasks(new HashSet<>(actPhaseTasks));
        actPhase.setComplete(complete);
        actPhase.setNextCycle(nextCycle);
        return actPhase;
    }
}
