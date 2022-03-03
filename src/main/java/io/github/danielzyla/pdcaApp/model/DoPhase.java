package io.github.danielzyla.pdcaApp.model;

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
@Table(name = "do_phases")
public class DoPhase {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startTime;

    @Column(length = 15000)
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "do_phase_id")
    private Set<DoPhaseTask> doPhaseTasks;

    private LocalDateTime endTime;

    private boolean complete;
}
