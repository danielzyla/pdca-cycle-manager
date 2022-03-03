package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.EmployeeReadDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteDto;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord o podanym id"
    );
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    public EmployeeService(final EmployeeRepository employeeRepository, DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
    }

    public EmployeeReadDto create(final EmployeeWriteDto employeeWriteDto) {
        Employee saved = employeeRepository.save(employeeWriteDto.toSave());
        return new EmployeeReadDto(saved);
    }

    public List<EmployeeReadDto> getAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeReadDto::new)
                .collect(Collectors.toList());
    }

    public Page<EmployeeReadDto> getAllPaged(Pageable pageable) {
        List<Employee> found = employeeRepository.findAll();
        Sublist sublist = new Sublist(pageable);
        return new PageImpl<>(
                sublist.getSublist(found).stream()
                        .map(EmployeeReadDto::new)
                        .collect(Collectors.toList()),
                pageable,
                found.size()
        );
    }

    public Page<EmployeeReadDto> searchByName(
            final String namePhrase,
            final String surnamePhrase,
            Pageable pageable
    ) {
        List<Employee> foundByName = employeeRepository.findByNameContainsOrSurnameContains(
                namePhrase,
                surnamePhrase
        );
        Sublist sublist = new Sublist(pageable);
        return new PageImpl<>(
                sublist.getSublist(foundByName).stream()
                        .map(EmployeeReadDto::new)
                        .collect(Collectors.toList()),
                pageable,
                foundByName.size()
        );
    }

    public EmployeeReadDto getById(Long id) {
        Optional<Employee> found = employeeRepository.findById(id);

        if (found.isPresent()) {
            return new EmployeeReadDto(found.get());
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public Employee getEmployeeById(Long id) {
        Optional<Employee> found = employeeRepository.findById(id);

        if (found.isPresent()) {
            return found.get();
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public void update(EmployeeWriteDto update) {
        Optional<Employee> found = employeeRepository.findById(update.getId());

        if (found.isPresent()) {
            Employee toSave = found.get();
            toSave.setName(update.getName());
            toSave.setSurname(update.getSurname());
            toSave.setEmail(update.getEmail());
            toSave.setDepartment(update.getDepartment());
            employeeRepository.save(toSave);
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public List<EmployeeReadDto> reducedByCurrent(Set<Employee> current) {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> employeesToReduce = new ArrayList<>();
        List<Employee> currentList = new ArrayList<>(current);
        for (Employee employee : allEmployees) {
            for (Employee e : currentList) {
                if (employee.getId() == e.getId()) {
                    employeesToReduce.add(employee);
                }
            }
        }
        allEmployees.removeAll(employeesToReduce);
        return allEmployees.stream()
                .map(EmployeeReadDto::new)
                .collect(Collectors.toList());
    }

    public void removeEmployee(Long id) {
        Optional<Employee> found = employeeRepository.findById(id);

        if (found.isPresent()) {
            employeeRepository.deleteById(id);
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public EmployeeWriteDto toEmployeeWriteDto(EmployeeWriteApiDto apiDto) {
        var employeeDto = new EmployeeWriteDto();
        employeeDto.setId(apiDto.getId());
        employeeDto.setName(apiDto.getName());
        employeeDto.setSurname(apiDto.getSurname());
        employeeDto.setEmail(apiDto.getEmail());
        employeeDto.setDepartment(departmentService.getDepartmentById(apiDto.getDepartmentId()));
        return employeeDto;
    }
}
