package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.Project;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DepartmentReadDto {
    private final int id;
    private final String deptName;
    private final List<Employee> employees;
    private final List<Project> projects;

    public DepartmentReadDto(Department source) {
        this.id = source.getId();
        this.deptName = source.getDeptName();
        this.employees = new ArrayList<>(source.getEmployees());
        this.projects = new ArrayList<>(source.getProjects());
    }
}
