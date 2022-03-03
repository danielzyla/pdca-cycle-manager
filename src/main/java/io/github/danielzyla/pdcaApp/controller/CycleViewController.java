package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.*;
import io.github.danielzyla.pdcaApp.model.*;
import io.github.danielzyla.pdcaApp.service.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/cycles")
class CycleViewController {
    CycleService cycleService;
    ProjectService projectService;
    PlanPhaseService planPhaseService;
    EmployeeService employeeService;
    DoPhaseService doPhaseService;
    DoPhaseTaskService doPhaseTaskService;
    CheckPhaseService checkPhaseService;
    ActPhaseService actPhaseService;
    ActPhaseTaskService actPhaseTaskService;

    @GetMapping("/showCycle/{id}")
    public String showCycle(Model model, @PathVariable("id") Long id) {
        CycleReadDto current = cycleService.getById(id);
        model.addAttribute("current", current);
        model.addAttribute(
                "tasksToShow",
                new ArrayList<>(current.getDoPhase().getDoPhaseTasks())
        );
        model.addAttribute("tasksByStartTime");
        model.addAttribute(
                "actPhaseTasksToShow",
                new ArrayList<>(current.getActPhase().getActPhaseTasks())
        );
        model.addAttribute("actPhaseTasksByStartTime");
        model.addAttribute("cyclesByNameAsc");
        return "showCycle";
    }

    @GetMapping("/addCycle/{projectId}")
    public String addCycle(
            Model model,
            @PathVariable("projectId") Long projectId
    ) {
        ProjectReadDto projectReadDto = projectService.getById(projectId);

        Comparator<Cycle> cyclesByNameAsc = sortCyclesByNameAsc();

        Cycle previousCycle = projectReadDto.getCycles()
                .stream()
                .sorted(cyclesByNameAsc).collect(Collectors.toList())
                .get(projectReadDto.getCycles().size() - 1);

        if(previousCycle.isComplete() && previousCycle.isNextCycle()) {
            Cycle cycle = cycleService.nextCycle(projectId);
            model.addAttribute("current", cycleService.getById(cycle.getId()));
            return "redirect:/cycles/showCycle/" + cycle.getId();
        } else {
            return "redirect:/cycles/showCycle/" + previousCycle.getId();
        }
    }

    @RequestMapping("/editPlan/{id}")
    public String updateCyclePlan(
            Model model,
            @PathVariable("id") Long id,
            @ModelAttribute("update") final PlanPhaseWriteDto update
    ) throws IllegalAccessException {
        if(cycleService.getById(id).getPlanPhase().isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            try {
                planPhaseService.updateCyclePlan(update);
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("current", cycleService.getById(id).getPlanPhase());
        return "editPlan";
    }

    @RequestMapping(path = "/editPlan/{id}", params = "addEmployee")
    public String addEmployee(
            Model model,
            @PathVariable("id") Long id,
            @ModelAttribute("update") final PlanPhaseWriteDto update
    ) {
        update.getEmployees().add(new Employee());
        model.addAttribute("infoAdd", "infoAdd");
        PlanPhase current = cycleService.getById(id).getPlanPhase();
        model.addAttribute(
                "allEmployees",
                employeeService.reducedByCurrent(current.getEmployees())
        );
        model.addAttribute("current", current);
        return "editPlan";
    }

    @RequestMapping(path = "/editPlan/{id}", params = "removeEmployee")
    public String removeEmployee(
            Model model,
            @PathVariable("id") Long id,
            @ModelAttribute("update") final PlanPhaseWriteDto update
    ) {
        update.getEmployees().add(new Employee());
        model.addAttribute("infoRemove", "infoRemove");
        PlanPhase current = cycleService.getById(id).getPlanPhase();
        model.addAttribute(
                "allEmployees",
                employeeService.reducedByCurrent(current.getEmployees())
        );
        model.addAttribute("current", current);
        return "editPlan";
    }

    @RequestMapping("/editDoPhase/{id}")
    public String updateCycleDoPhase(
            Model model,
            @PathVariable("id") Long id,
            final DoPhaseWriteDto update
    ) throws IllegalAccessException {
        if(cycleService.getById(id).getDoPhase().isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            doPhaseService.updateCycleDoPhase(update);
        }
        model.addAttribute("tasksByStartTime");
        model.addAttribute("current", cycleService.getById(id).getDoPhase());
        return "editDoPhase";
    }

    @RequestMapping(path = "/editDoPhase/{id}", params = "addTask")
    public String addTask(
            Model model,
            @PathVariable("id") Long id,
            final DoPhaseWriteDto update
    ) throws IllegalAccessException {
        if(cycleService.getById(id).getDoPhase().isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            update.getDoPhaseTasks().add(doPhaseTaskService.create(new DoPhaseTaskWriteDto()));
            doPhaseService.addTaskIntoDoPhase(update);
        }
        model.addAttribute("current", cycleService.getById(id).getDoPhase());
        return "redirect:/cycles/editDoPhase/" + id;
    }

    @RequestMapping("/editCheckPhase/{id}")
    public String updateCycleCheckPhase(
            Model model,
            @PathVariable("id") Long id,
            final CheckPhaseWriteDto update
    ) throws IllegalAccessException {
        if(cycleService.getById(id).getCheckPhase().isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            checkPhaseService.updateCycleCheckPhase(update);
        }
        model.addAttribute("current", cycleService.getById(id).getCheckPhase());
        return "editCheckPhase";
    }

    @RequestMapping("/editActPhase/{id}")
    public String updateCycleActPhase(
            Model model,
            @PathVariable("id") Long id,
            final ActPhaseWriteDto update
    ) throws IllegalAccessException {
        if(cycleService.getById(id).getActPhase().isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            actPhaseService.updateCycleActPhase(update);
        }
        model.addAttribute("current", cycleService.getById(id).getActPhase());
        return "editActPhase";
    }

    @RequestMapping(path = "/editActPhase/{id}", params = "addActPhaseTask")
    public String addActPhaseTask(
            Model model,
            @PathVariable("id") Long id,
            final ActPhaseWriteDto update
    ) throws IllegalAccessException {
        if(cycleService.getById(id).getActPhase().isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            update.getActPhaseTasks().add(actPhaseTaskService.create(new ActPhaseTaskWriteDto()));
            actPhaseService.addTaskIntoActPhase(update);
        }
        model.addAttribute("current", cycleService.getById(id).getActPhase());
        return "editActPhase";
    }

    @ModelAttribute("cyclesByNameAsc")
    public Comparator<Cycle> sortCyclesByNameAsc() {
        return (cycle1, cycle2) ->
                String.CASE_INSENSITIVE_ORDER.compare(cycle1.getCycleName(), cycle2.getCycleName());
    }

    @ModelAttribute("tasksByStartTime")
    public Comparator<DoPhaseTask> sortTasksByStartTime() {
        return Comparator.comparing(Task::getStartTime);
    }

    @ModelAttribute("actPhaseTasksByStartTime")
    public Comparator<ActPhaseTask> sortActPhaseTasksByStartTime() {
        return Comparator.comparing(Task::getStartTime);
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
