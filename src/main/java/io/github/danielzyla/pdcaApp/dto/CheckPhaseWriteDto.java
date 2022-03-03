package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.CheckPhase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class CheckPhaseWriteDto {
    private Long id;
    private String conclusions, achievements, nextSteps;
    private boolean complete;

    public CheckPhase toSave() {
        var checkPhase = new CheckPhase();
        checkPhase.setConclusions(conclusions);
        checkPhase.setAchievements(achievements);
        checkPhase.setNextSteps(nextSteps);
        checkPhase.setComplete(complete);
        return checkPhase;
    }
}
