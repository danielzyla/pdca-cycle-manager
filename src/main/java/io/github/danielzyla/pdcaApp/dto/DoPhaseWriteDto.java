package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.DoPhase;
import io.github.danielzyla.pdcaApp.model.DoPhaseTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class DoPhaseWriteDto {
    private long id;
    private String description;
    private List<DoPhaseTask> doPhaseTasks = new ArrayList<>();
    private boolean complete;

    public DoPhase toSave() {
        var doPhase = new DoPhase();
        doPhase.setDescription(description);
        doPhase.setDoPhaseTasks(new HashSet<>(doPhaseTasks));
        doPhase.setComplete(complete);
        return doPhase;
    }
}
