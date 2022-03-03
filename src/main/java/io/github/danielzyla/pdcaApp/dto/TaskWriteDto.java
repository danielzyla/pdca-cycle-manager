package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class TaskWriteDto {
    private long id;
    private LocalDateTime startTime;
    private String description;
    private List<Long> employeeIds;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    private boolean complete;
    private LocalDateTime executionTime;
}
