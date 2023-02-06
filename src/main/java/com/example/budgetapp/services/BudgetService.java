package com.example.budgetapp.services;

public interface BudgetService {

    int getDailyBudget();

    int getBalance();

    int getVacationBonus(int daysCount);

    int getSalaryWithVacation(int vacationDaysCount, int vacationWorkingDaysCount, int workingDaysInMonth);
}
