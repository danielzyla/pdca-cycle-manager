package io.github.danielzyla.pdcaApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.model.Project;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Getter @Setter
public class ProjectWriteDto {
    private long id;
    @Setter(value = AccessLevel.NONE)
    private LocalDateTime startTime;
    @NotBlank(message = "pole wymagane")
    private String projectName, projectCode;
    private List<Department> departments = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    @JsonIgnore
    private List<Cycle> cycles = new ArrayList<>();
    private LocalDateTime endTime;
    private boolean complete;

    public ProjectWriteDto() {
        this.cycles.add(new Cycle());
    }

    public Project toSave() {
        var project = new Project();
        project.setProjectName(projectName);
        project.setProjectCode(projectCode);
        project.setDepartments(new HashSet<>(departments));
        project.setProducts(new HashSet<>(products));
        project.setCycles(new LinkedHashSet<>(cycles));
        project.setEndTime(endTime);
        project.setComplete(complete);
        return project;
    }
}
