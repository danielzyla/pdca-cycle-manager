package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.ProjectReadDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteDto;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.model.Project;
import io.github.danielzyla.pdcaApp.repository.ProjectRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord o podanym id"
    );
    private final ProjectRepository projectRepository;
    private final DepartmentService departmentService;
    private final ProductService productService;

    ProjectService(final ProjectRepository projectRepository, DepartmentService departmentService, ProductService productService) {
        this.projectRepository = projectRepository;
        this.departmentService = departmentService;
        this.productService = productService;
    }

    public ProjectReadDto createNewProject(final ProjectWriteDto projectWriteDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LocalDateTime timeNow = LocalDateTime.now();
        Cycle firstCycle = projectWriteDto.getCycles().get(0);
        firstCycle.setCycleName("cykl_1");
        firstCycle.setStartTime(timeNow);
        firstCycle.getPlanPhase().setStartTime(timeNow);
        firstCycle.getPlanPhase().setName(
                firstCycle.getCycleName() + " / " + projectWriteDto.getProjectName()
        );
        firstCycle.setComplete(false);

        var project = projectWriteDto.toSave();
        project.setStartTime(timeNow);
        project.setUsername(username);
        project.setComplete(false);
        return new ProjectReadDto(projectRepository.save(project));
    }

    public List<ProjectReadDto> getAll() {
        return projectRepository.findAll().stream()
                .map(ProjectReadDto::new)
                .collect(Collectors.toList());
    }

    public List<ProjectReadDto> getAllByUsername(String username) {
        return projectRepository.findAllByUsername(username).stream()
                .map(ProjectReadDto::new)
                .collect(Collectors.toList());
    }

    public ProjectReadDto getById(Long id) {
        Optional<Project> project = projectRepository.findById(id);

        if (project.isPresent()) {
            return new ProjectReadDto(project.get());
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void removeProject(Long id) {
        Optional<Project> project = projectRepository.findById(id);

        if (project.isPresent()) {
            projectRepository.deleteById(id);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public void updateProject(final ProjectWriteDto toUpdateDto) {
        Optional<Project> foundById = projectRepository.findById(toUpdateDto.getId());

        if (foundById.isPresent()) {
            Project toUpdate = foundById.get();
            toUpdate.setProjectName(toUpdateDto.getProjectName());
            toUpdate.setProjectCode(toUpdateDto.getProjectCode());
            toUpdate.setDepartments(new HashSet<>(toUpdateDto.getDepartments()));
            toUpdate.setProducts(new HashSet<>(toUpdateDto.getProducts()));
            projectRepository.save(toUpdate);
        } else {
            throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
    }

    public ProjectWriteDto toProjectWriteDto(ProjectWriteApiDto apiDto) {
        ProjectWriteDto toSave = new ProjectWriteDto();
        if (apiDto.getId() != null) toSave.setId(apiDto.getId());
        toSave.setProjectName(apiDto.getProjectName());
        toSave.setProjectCode(apiDto.getProjectCode());
        List<Department> departmentList = departmentService.getDepartmentList(apiDto.getDepartmentIds());
        toSave.setDepartments(departmentList);
        List<Product> productList = productService.getProductList(apiDto.getProductIds());
        toSave.setProducts(productList);
        return toSave;
    }

}
