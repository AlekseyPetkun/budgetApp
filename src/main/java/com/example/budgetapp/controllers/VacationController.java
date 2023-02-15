package com.example.budgetapp.controllers;

import com.example.budgetapp.services.BudgetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vacation")
public class VacationController {

    private final BudgetService budgetService;

    public VacationController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public int vacationBonus(@RequestParam int vacDays) {
        return budgetService.getVacationBonus(vacDays);
    }

    @GetMapping("/salary")
    public int salaryWithVacation(@RequestParam int vacDays, @RequestParam int workDays,
                                  @RequestParam int vacWorkDays) {
        return budgetService.getSalaryWithVacation(vacDays, vacWorkDays, workDays);
    }
}
