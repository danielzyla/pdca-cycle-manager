package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.CycleReadDto;
import io.github.danielzyla.pdcaApp.dto.CycleWriteDto;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Project;
import io.github.danielzyla.pdcaApp.repository.CycleRepository;
import io.github.danielzyla.pdcaApp.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CycleService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );
    private final CycleRepository cycleRepository;
    private final ProjectRepository projectRepository;

    public List<CycleReadDto> getAll() {
        return cycleRepository.findAll().stream()
                .map(CycleReadDto::new)
                .collect(Collectors.toList());
    }

    public CycleReadDto getById(final Long id) {
        Optional<Cycle> cycle = cycleRepository.findById(id);

        if(cycle.isPresent()) {
            return new CycleReadDto(cycle.get());
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public CycleReadDto save(final CycleWriteDto cycle) {
        return new CycleReadDto(cycleRepository.save(cycle.toSave()));
    }

    public Cycle nextCycle(Long id) {
        Cycle cycle = new Cycle();
        LocalDateTime timeNow = LocalDateTime.now();
        cycle.setStartTime(timeNow);
        cycle.getPlanPhase().setStartTime(timeNow);
        Optional<Project> found = projectRepository.findById(id);

        if(found.isPresent()) {
            Project project = found.get();
            cycle.setProject(project);
            cycle.setCycleName("cykl_" + (project.getCycles().size() + 1));
            cycle.getPlanPhase().setName(
                    cycle.getCycleName() + " / " + project.getProjectName()
            );
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;

        return cycleRepository.save(cycle);
    }
}
