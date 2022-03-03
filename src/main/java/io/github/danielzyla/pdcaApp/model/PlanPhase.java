package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "plan_phases")
public class PlanPhase {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startTime;

    private String name;

    @Column(length = 3000)
    private String problemDescription;

    @Column(length = 3000)
    private String currentSituationAnalysis;

    @Column(length = 3000)
    private String goal;

    @Column(length = 3000)
    private String rootCauseIdentification;

    @Column(length = 3000)
    private String optimalSolutionChoice;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "plan_phases_employees",
            joinColumns = @JoinColumn(name = "plan_phase_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @ToString.Exclude
    private Set<Employee> employees = new HashSet<>();

    private LocalDateTime endTime;

    private boolean complete;
}
