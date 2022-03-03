package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "employees")
public class Employee implements Serializable {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "must not be blank")
    private String name, surname;

    @NotBlank(message = "must not be blank")
    @Email
    private String email;

    @ManyToMany(mappedBy = "employees")
    @JsonBackReference(value = "employee-planPhases")
    private Set<PlanPhase> planPhases = new HashSet<>();

    @ManyToMany(mappedBy = "employees")
    @JsonBackReference(value = "employee-tasks")
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Employee(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Employee employee = (Employee) o;
        return id == employee.id
                && Objects.equals(name, employee.name)
                && Objects.equals(surname, employee.surname)
                && Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email);
    }

    @Override
    public String toString() {
        return name + " " + surname + ", " + email + ", " + department;
    }
}
