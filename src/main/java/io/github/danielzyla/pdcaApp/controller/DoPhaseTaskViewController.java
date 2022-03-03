package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.DoPhaseTaskWriteDto;
import io.github.danielzyla.pdcaApp.model.DoPhaseTask;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.service.DoPhaseService;
import io.github.danielzyla.pdcaApp.service.DoPhaseTaskService;
import io.github.danielzyla.pdcaApp.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/tasks")
class DoPhaseTaskViewController {
    DoPhaseTaskService doPhaseTaskService;
    EmployeeService employeeService;
    DoPhaseService doPhaseService;

    @RequestMapping("/editTask/{id}/{phaseId}")
    public String editTask(
            Model model,
            @PathVariable("id") Long id,
            @PathVariable("phaseId") Long phaseId,
            @ModelAttribute("taskForm") final DoPhaseTaskWriteDto taskForm
    ) throws IllegalAccessException {
        if(doPhaseService.getById(phaseId).isComplete()) {
            throw new IllegalAccessException("etap zakończony");
        } else {
            doPhaseTaskService.updateTask(id, taskForm);
        }
        model.addAttribute("current", doPhaseTaskService.getById(id));
        return "editTask";
    }

    @RequestMapping(path = "/editTask/{id}/{phaseId}", params = "addEmployee")
    public String addEmployee(
            Model model,
            @PathVariable("id") Long id,
            @PathVariable("phaseId") Long phaseId,
            @ModelAttribute("taskForm") final DoPhaseTaskWriteDto taskForm
    ) {
        taskForm.getEmployees().add(new Employee());
        model.addAttribute("infoAdd", "infoAdd");
        DoPhaseTask current = doPhaseTaskService.getById(id);
        model.addAttribute(
                "allEmployees",
                employeeService.reducedByCurrent(current.getEmployees())
        );
        model.addAttribute("current", current);
        return "editTask";
    }

    @RequestMapping(path = "/editTask/{id}/{phaseId}", params = "removeEmployee")
    public String removeEmployee(
            Model model,
            @PathVariable("id") Long id,
            @PathVariable("phaseId") Long phaseId,
            @ModelAttribute("taskForm") final DoPhaseTaskWriteDto taskForm
    ) {
        taskForm.getEmployees().add(new Employee());
        model.addAttribute("infoRemove", "infoRemove");
        model.addAttribute("current", doPhaseTaskService.getById(id));
        model.addAttribute("phaseId", phaseId);
        return "editTask";
    }

    @GetMapping("/removeTask/{taskId}/{phaseId}")
    public String removeTask(
            @PathVariable("taskId") final Long taskId,
            @PathVariable("phaseId") final Long phaseId
    ) {
        doPhaseTaskService.removeTask(taskId);
        return "redirect:/cycles/editDoPhase/" + phaseId;
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
