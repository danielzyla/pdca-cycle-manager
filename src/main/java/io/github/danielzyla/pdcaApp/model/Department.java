package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "departments")
public class Department implements Serializable {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "must not be blank")
    private String deptName;

    @OneToMany
    @JoinColumn(name = "department_id")
    @JsonBackReference(value = "department-employees")
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(mappedBy = "departments")
    @JsonBackReference(value = "department-projects")
    private Set<Project> projects = new HashSet<>();

    public Department(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return "" + deptName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return id == that.id && Objects.equals(deptName, that.deptName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deptName);
    }
}
