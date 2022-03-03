package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter @Setter
public class EmployeeWriteDto {
    private long id;
    @NotBlank(message = "pole wymagane")
    private String name, surname;
    @NotBlank(message = "pole wymagane")
    @Email
    private String email;
    private Department department;

    public Employee toSave() {
        var employee = new Employee();
        employee.setName(name);
        employee.setSurname(surname);
        employee.setEmail(email);
        employee.setDepartment(department);
        return employee;
    }
}
