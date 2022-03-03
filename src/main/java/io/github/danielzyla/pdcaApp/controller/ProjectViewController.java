package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.DepartmentReadDto;
import io.github.danielzyla.pdcaApp.dto.ProductReadDto;
import io.github.danielzyla.pdcaApp.dto.ProjectReadDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteDto;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.service.DepartmentService;
import io.github.danielzyla.pdcaApp.service.ProductService;
import io.github.danielzyla.pdcaApp.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/projects")
class ProjectViewController {
    private final ProjectService projectService;
    private final DepartmentService departmentService;
    private final ProductService productService;

    @GetMapping
    public String showProjects(Model model) {
        model.addAttribute("allProjects");
        model.addAttribute("allDepartments");
        model.addAttribute("allProducts");
        model.addAttribute("departmentsByNameAsc");
        model.addAttribute("productsByNameAsc");
        return "projects";
    }

    @PostMapping(params = "addProject")
    public String initProjectForm(Model model) {
        model.addAttribute("info", "newForm");
        model.addAttribute("project", new ProjectWriteDto());
        model.addAttribute("departmentsByNameAsc");
        model.addAttribute("departmentsDtoByNameAsc");
        model.addAttribute("productsByNameAsc");
        model.addAttribute("productsDtoByNameAsc");
        return "projects";
    }

    @PostMapping
    public String createNewProject(
            Model model,
            @Valid @ModelAttribute("project") final ProjectWriteDto toSave,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("info", "keepForm");
            return "projects";
        }

        try {
            projectService.createNewProject(toSave);
        } catch (DataIntegrityViolationException e) {
            String message = "Istnieje już w bazie taki rekord: " + e.getMostSpecificCause();
            model.addAttribute("info", "keepForm");
            model.addAttribute("message_project", message);
            return "projects";
        }
        return "redirect:projects";
    }

    @RequestMapping("/update/{id}")
    public String updateProject(
            Model model,
            @PathVariable("id") Long id,
            @Valid final ProjectWriteDto toUpdate,
            BindingResult bindingResult
    ) throws IllegalAccessException {
        ProjectReadDto current = projectService.getById(id);

        if(current.isComplete()) {
            throw new IllegalAccessException("projekt zakończony");
        }
        model.addAttribute("current", current);
        model.addAttribute("allDepartments");
        model.addAttribute("allProducts");

        if(bindingResult.hasErrors()) {
            return "update";
        }

        try {
            projectService.updateProject(toUpdate);
            return "redirect:/projects/update/{id}";
        } catch (DataIntegrityViolationException e) {
            String message = "Istnieje już w bazie taki rekord: " + e.getMostSpecificCause();
            model.addAttribute("message_project", message);
            return "update";
        }
    }

    @GetMapping("/{id}")
    public String removeProject(@PathVariable("id") final Long id) {
        projectService.removeProject(id);
        return "redirect:";
    }

    @ModelAttribute("allProjects")
    public List<ProjectReadDto> getAllProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("admin")) {
            return projectService.getAll();
        } else {
            return projectService.getAllByUsername(username);
        }
    }

    @ModelAttribute("allDepartments")
    public List<DepartmentReadDto> getAllDepartments() {
        return departmentService.getAll();
    }

    @ModelAttribute("allProducts")
    public List<ProductReadDto> getAllProducts() {
        return productService.getAll();
    }

    @ModelAttribute("departmentsByNameAsc")
    public Comparator<Department> sortDepartmentsByNameAsc() {
        return (
                Department d1, Department d2) ->
                String.CASE_INSENSITIVE_ORDER.compare(d1.getDeptName(), d2.getDeptName()
        );
    }

    @ModelAttribute("departmentsDtoByNameAsc")
    public Comparator<DepartmentReadDto> sortDepartmentsDtoByNameAsc() {
        return (
                DepartmentReadDto d1, DepartmentReadDto d2) ->
                String.CASE_INSENSITIVE_ORDER.compare(d1.getDeptName(), d2.getDeptName()
                );
    }

    @ModelAttribute("productsByNameAsc")
    public Comparator<Product> sortProductsByNameAsc() {
        return (
                Product p1, Product p2) ->
                String.CASE_INSENSITIVE_ORDER.compare(p1.getProductName(), p2.getProductName()
        );
    }

    @ModelAttribute("productsDtoByNameAsc")
    public Comparator<ProductReadDto> sortProductsDtoByNameAsc() {
        return (
                ProductReadDto p1, ProductReadDto p2) ->
                String.CASE_INSENSITIVE_ORDER.compare(p1.getProductName(), p2.getProductName()
                );
    }

    @ModelAttribute("cyclesByNameAsc")
    public Comparator<Cycle> sortCyclesByNameAsc() {
        return (cycle1, cycle2) ->
                String.CASE_INSENSITIVE_ORDER.compare(cycle1.getCycleName(), cycle2.getCycleName());
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
