package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "projects")
public class Project implements Serializable {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private LocalDateTime startTime;

    @NotBlank (message = "must not be blank")
    @Column(unique = true)
    private String projectName, projectCode;

    @ManyToMany
    @JoinTable(
            name = "projects_departments",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns = @JoinColumn(name="department_id")
    )
    @JsonBackReference("project-departments")
    private Set<Department> departments = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "projects_products",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns = @JoinColumn(name="product_id")
    )
    @JsonBackReference("project-products")
    private Set<Product> products = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    @JsonBackReference("project-cycles")
    private Set<Cycle> cycles = new LinkedHashSet<>();

    private LocalDateTime endTime;

    private boolean complete;

    @Override
    public String toString() {
        return "" + projectName;
    }
}
