package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.PlanPhase;
import io.github.danielzyla.pdcaApp.model.Task;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EmployeeReadDto {
    private final long id;
    private final String name, surname, email;
    private final List<PlanPhase> planPhases;
    private final List<Task> tasks;
    private final Department department;

    public EmployeeReadDto(final Employee source) {
        this.id = source.getId();
        this.name = source.getName();
        this.surname = source.getSurname();
        this.email = source.getEmail();
        this.planPhases = new ArrayList<>(source.getPlanPhases());
        this.tasks = new ArrayList<>(source.getTasks());
        this.department = source.getDepartment();
    }

    @Override
    public String toString() {
        return name + " " + surname + " " + id;
    }
}
