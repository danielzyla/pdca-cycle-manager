package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.DepartmentReadDto;
import io.github.danielzyla.pdcaApp.dto.DepartmentWriteDto;
import io.github.danielzyla.pdcaApp.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/departments")
class DepartmentViewController {
    private static final int PAGE_SIZE = 5;
    DepartmentService departmentService;

    DepartmentViewController(final DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String showDepartments(
            Model model,
            @RequestParam(value = "deptNamePhrase") Optional<String> givenPhrase,
            @RequestParam(defaultValue = "0") String currentPage
    ) {
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), PAGE_SIZE);
        Page<DepartmentReadDto> searchResult;

        if(givenPhrase.isPresent()) {
            searchResult = departmentService.searchByName(givenPhrase.get(), pageable);
            model.addAttribute("givenPhrase", givenPhrase.get());
        }
        else {
            searchResult = departmentService.getAllPaged(pageable);
        }

        model.addAttribute("foundDepartments", searchResult);
        model.addAttribute("pageNumbers", searchResult.getTotalPages());
        return "departments";
    }

    @PostMapping(params = "addDepartment")
    public String initDeptForm(Model model) {
        model.addAttribute("department", new DepartmentWriteDto());
        model.addAttribute("info", "newForm");
        return "departments";
    }

    @PostMapping
    public String createDepartment(
            Model model,
            @Valid @ModelAttribute("department") DepartmentWriteDto toSave,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("info", "keepForm");
            return "departments";
        }
        departmentService.create(toSave);
        return "departments";
    }

    @RequestMapping("/editDepartment/{id}")
    public String editDepartment(
            Model model,
            @PathVariable("id") Integer id,
            @Valid final DepartmentWriteDto update,
            BindingResult bindingResult
    ) {
        model.addAttribute("current", departmentService.getById(id));

        if(bindingResult.hasErrors()) {
            return "editDepartment";
        }

        departmentService.update(update);
        return "redirect:/departments/editDepartment/" + id;
    }

    @GetMapping("/{id}")
    public String removeDepartment(Model model, @PathVariable("id") Integer id) {
        try{
            departmentService.removeDepartment(id);
        } catch(Exception e) {
            String message;
            if(e.getCause() != null) {
                message = "Dział może być przypisany do projektu; " + e.getCause();
            } else message = "Nie znaleziono działu dla podanego id";
            model.addAttribute("error", message);
            return "error";
        }
        return "redirect:";
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
