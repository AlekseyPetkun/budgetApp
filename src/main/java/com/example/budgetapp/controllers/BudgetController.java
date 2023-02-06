package com.example.budgetapp.controllers;

import com.example.budgetapp.services.BudgetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/dailyBudget")
    public int dailyBudget() {
        return budgetService.getDailyBudget();
    }

    @GetMapping("/balance")
    public int balance() {
        return budgetService.getBalance();
    }

    @GetMapping("/vacation")
    public int vacationBonus(@RequestParam int vacDays) {
        return budgetService.getVacationBonus(vacDays);
    }

    @GetMapping("/vacation/salary")
    public int salaryWithVacation(@RequestParam int vacDays, @RequestParam int workDays,
                                  @RequestParam int vacWorkDays) {
        return budgetService.getSalaryWithVacation(vacDays, vacWorkDays, workDays);
    }
}
