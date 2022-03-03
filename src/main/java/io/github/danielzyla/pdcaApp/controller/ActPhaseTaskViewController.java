package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.ActPhaseTaskWriteDto;
import io.github.danielzyla.pdcaApp.model.ActPhaseTask;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.service.ActPhaseService;
import io.github.danielzyla.pdcaApp.service.ActPhaseTaskService;
import io.github.danielzyla.pdcaApp.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/actPhaseTasks")
class ActPhaseTaskViewController {
    ActPhaseTaskService actPhaseTaskService;
    EmployeeService employeeService;
    ActPhaseService actPhaseService;

    @RequestMapping("/editActPhaseTask/{id}/{phaseId}")
    public String editTask(
            Model model,
            @PathVariable("id") Long id,
            @PathVariable("phaseId") Long phaseId,
            @ModelAttribute("taskForm") final ActPhaseTaskWriteDto taskForm
    ) throws IllegalAccessException {
        if(actPhaseService.getById(phaseId).isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            actPhaseTaskService.updateTask(id, taskForm);
        }
        model.addAttribute("current", actPhaseTaskService.getById(id));
        model.addAttribute("phaseId", phaseId);
        return "editActPhaseTask";
    }

    @RequestMapping(path = "/editActPhaseTask/{id}/{phaseId}", params = "addEmployee")
    public String addEmployee(
            Model model,
            @PathVariable("id") Long id,
            @PathVariable("phaseId") Long phaseId,
            @ModelAttribute("taskForm") final ActPhaseTaskWriteDto taskForm
    ) {
        taskForm.getEmployees().add(new Employee());
        model.addAttribute("infoAdd", "infoAdd");
        ActPhaseTask current = actPhaseTaskService.getById(id);
        model.addAttribute(
                "allEmployees",
                employeeService.reducedByCurrent(current.getEmployees())
        );
        model.addAttribute("current", current);
        return "editActPhaseTask";
    }

    @RequestMapping(path = "/editActPhaseTask/{id}/{phaseId}", params = "removeEmployee")
    public String removeEmployee(
            Model model,
            @PathVariable("id") Long id,
            @PathVariable("phaseId") Long phaseId,
            @ModelAttribute("taskForm") final ActPhaseTaskWriteDto taskForm
    ) {
        taskForm.getEmployees().add(new Employee());
        model.addAttribute("infoRemove", "infoRemove");
        model.addAttribute("current", actPhaseTaskService.getById(id));
        return "editActPhaseTask";
    }

    @GetMapping("/removeTask/{taskId}/{phaseId}")
    public String removeTask(
            @PathVariable("taskId") final Long taskId,
            @PathVariable("phaseId") final Long phaseId
    ) {
        actPhaseTaskService.removeTask(taskId);
        return "redirect:/cycles/editActPhase/" + phaseId;
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }

}
