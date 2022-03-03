package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "act_phases")
public class ActPhase {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startTime;

    @Column(length = 15000)
    private String description;

    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "act_phase_id")
    private Set<ActPhaseTask> actPhaseTasks;

    private LocalDateTime endTime;

    private boolean complete;

    private boolean nextCycle;
}
