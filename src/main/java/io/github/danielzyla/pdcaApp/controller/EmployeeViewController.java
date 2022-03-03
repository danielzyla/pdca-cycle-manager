package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.DepartmentReadDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeReadDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteDto;
import io.github.danielzyla.pdcaApp.service.DepartmentService;
import io.github.danielzyla.pdcaApp.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
@RequestMapping("/employees")
class EmployeeViewController {
    private static final int PAGE_SIZE = 5;
    EmployeeService employeeService;
    DepartmentService departmentService;

    @GetMapping
    public String showEmployees(
            Model model,
            @RequestParam(value = "employeeNameOrSurnamePhrase") Optional<String> givenPhrase,
            @RequestParam(defaultValue = "0") String currentPage
    ) {
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), PAGE_SIZE);
        Page<EmployeeReadDto> searchResult;

        if (givenPhrase.isPresent()) {
            searchResult = employeeService.searchByName(givenPhrase.get(), givenPhrase.get(), pageable);
            model.addAttribute("givenPhrase", givenPhrase.get());
        } else {
            searchResult = employeeService.getAllPaged(pageable);
        }

        model.addAttribute("foundEmployees", searchResult);
        model.addAttribute("pageNumbers", searchResult.getTotalPages());
        return "employees";
    }

    @PostMapping(params = "addEmployee")
    public String initEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeWriteDto());
        model.addAttribute("info", "newForm");
        return "employees";
    }

    @PostMapping
    public String createEmployee(
            Model model,
            @Valid @ModelAttribute("employee") EmployeeWriteDto toSave,
            BindingResult bindingResult
    ) {
        model.addAttribute("allDepartments");

        if (bindingResult.hasErrors()) {
            model.addAttribute("info", "keepForm");
            return "employees";
        }

        employeeService.create(toSave);
        return "employees";
    }

    @RequestMapping("/editEmployee/{id}")
    public String editEmployee(
            Model model,
            @PathVariable("id") Long id,
            @Valid final EmployeeWriteDto update,
            BindingResult bindingResult
    ) {
        model.addAttribute("current", employeeService.getById(id));
        if (bindingResult.hasErrors()) {
            return "editEmployee";
        }

        employeeService.update(update);
        return "redirect:/employees/editEmployee/" + id;
    }

    @GetMapping("/{id}")
    public String removeEmployee(Model model, @PathVariable("id") Long id) {
        try {
            employeeService.removeEmployee(id);
        } catch (Exception e) {
            String message;
            if (e.getCause() != null) {
                message = "Osoba może być przypisana do projektu lub do zadania; " + e.getCause();
            } else message = "Nie znaleziono użytkownika dla podanego id";
            model.addAttribute("error", message);
            return "error";
        }
        return "redirect:";
    }

    @ModelAttribute("allDepartments")
    public List<DepartmentReadDto> getAllDepartments() {
        return departmentService.getAll();
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
