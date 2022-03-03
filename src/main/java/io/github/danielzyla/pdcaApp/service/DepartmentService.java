package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.DepartmentReadDto;
import io.github.danielzyla.pdcaApp.dto.DepartmentWriteDto;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );
    private final DepartmentRepository departmentRepository;

    DepartmentService(final DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentReadDto create(final DepartmentWriteDto departmentWriteDto) {
        var department = departmentWriteDto.toSave();
        return new DepartmentReadDto(departmentRepository.save(department));
    }

    public List<DepartmentReadDto> getAll() {
        return departmentRepository.findAll().stream()
                .map(DepartmentReadDto::new)
                .collect(Collectors.toList());
    }

    public Page<DepartmentReadDto> getAllPaged(Pageable pageable) {
        List<Department> found = departmentRepository.findAll();
        Sublist sublist = new Sublist(pageable);
        return new PageImpl<>(
                sublist.getSublist(found).stream()
                        .map(DepartmentReadDto::new)
                        .collect(Collectors.toList()),
                pageable,
                found.size()
        );
    }

    public Page<DepartmentReadDto> searchByName(final String phrase, Pageable pageable) {
        List<Department> foundByName = departmentRepository.findByDeptNameContains(phrase);
        Sublist sublist = new Sublist(pageable);
        return new PageImpl<>(
                sublist.getSublist(foundByName).stream()
                        .map(DepartmentReadDto::new)
                        .collect(Collectors.toList()),
                pageable,
                foundByName.size()
        );
    }

    public DepartmentReadDto getById(final Integer id) {
        Optional<Department> found = departmentRepository.findById(id);
        if (found.isPresent()) {
            return new DepartmentReadDto(found.get());
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public Department getDepartmentById(final Integer id) {
        Optional<Department> found = departmentRepository.findById(id);
        if (found.isPresent()) {
            return found.get();
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;

    }

    public void update(final DepartmentWriteDto update) {
        Optional<Department> found = departmentRepository.findById(update.getId());
        if (found.isPresent()) {
            Department toSave = found.get();
            toSave.setDeptName(update.getDeptName());
            departmentRepository.save(toSave);
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    List<Department> getDepartmentList(List<Integer> departmentsIds) {
        List<Department> departmentList = new ArrayList<>();
        for (Integer id : departmentsIds) {
            Optional<Department> optionalDepartment = departmentRepository.findById(id);
            if (optionalDepartment.isPresent()) {
                departmentList.add(optionalDepartment.get());
            } else throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
        return departmentList;
    }

    public void removeDepartment(final int id) {
        Optional<Department> found = departmentRepository.findById(id);
        if (found.isPresent()) {
            departmentRepository.deleteById(id);
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }
}
