package io.github.danielzyla.pdcaApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@Getter @Setter
@Entity
@DiscriminatorValue("act_phase_task")
public class ActPhaseTask extends Task{
}
