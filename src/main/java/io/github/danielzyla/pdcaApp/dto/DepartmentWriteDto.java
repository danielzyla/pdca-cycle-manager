package io.github.danielzyla.pdcaApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class DepartmentWriteDto {
    private int id;
    @NotBlank(message = "pole wymagane")
    private String deptName;
    @JsonIgnore
    private List<Employee> employees = new ArrayList<>();

    public Department toSave() {
        var department = new Department();
        department.setDeptName(deptName);
        department.setEmployees(new HashSet<>(employees));
        return department;
    }
}
