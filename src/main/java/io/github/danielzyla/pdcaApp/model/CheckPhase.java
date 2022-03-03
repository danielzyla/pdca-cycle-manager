package io.github.danielzyla.pdcaApp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "check_phases")
public class CheckPhase {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startTime;

    @Column(length = 5000)
    private String conclusions;

    @Column(length = 5000)
    private String achievements;

    @Column(length = 5000)
    private String nextSteps;

    private LocalDateTime endTime;

    private boolean complete;
}
