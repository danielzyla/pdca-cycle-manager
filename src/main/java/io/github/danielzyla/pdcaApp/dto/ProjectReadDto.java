package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.model.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter @ToString
public class ProjectReadDto {
    private long id;
    private LocalDateTime startTime;
    private String projectName;
    private String projectCode;
    private List<Department> departments;
    private List<Product> products;
    private List<Cycle> cycles;
    private LocalDateTime endTime;
    private boolean complete;

    public ProjectReadDto(final Project source) {
        this.id = source.getId();
        this.startTime = source.getStartTime();
        this.projectName = source.getProjectName();
        this.projectCode = source.getProjectCode();
        this.departments = new ArrayList<>(source.getDepartments());
        this.products = new ArrayList<>(source.getProducts());
        this.cycles = new ArrayList<>(source.getCycles());
        this.endTime = source.getEndTime();
        this.complete = source.isComplete();
    }
}
