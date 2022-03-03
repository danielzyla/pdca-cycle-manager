package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.ActPhaseTask;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class ActPhaseTaskWriteDto {
    private LocalDateTime startTime;
    private String description;
    private List<Employee> employees = new ArrayList<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    private boolean complete;
    private LocalDateTime executionTime;

    public ActPhaseTask toSave() {
        var task = new ActPhaseTask();
        task.setStartTime(startTime);
        task.setDescription(description);
        task.setEmployees(new HashSet<>(employees));
        task.setDeadline(deadline);
        task.setTaskStatus(taskStatus);
        task.setComplete(complete);
        task.setExecutionTime(executionTime);
        return task;
    }
}
