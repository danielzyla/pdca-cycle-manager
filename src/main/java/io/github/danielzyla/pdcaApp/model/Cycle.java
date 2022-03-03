package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "cycles")
public class Cycle {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cycleName;

    private LocalDateTime startTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference(value = "cycle-planPhase")
    private PlanPhase planPhase = new PlanPhase();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference(value = "cycle-doPhase")
    private DoPhase doPhase = new DoPhase();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference(value = "cycle-checkPhase")
    private CheckPhase checkPhase = new CheckPhase();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference(value = "cycle-actPhase")
    private ActPhase actPhase = new ActPhase();

    private LocalDateTime endTime;

    private boolean complete;

    private boolean nextCycle;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference(value = "cycle-project")
    private Project project;

    @Override
    public String toString() {
        return "" + cycleName;
    }
}
