package com.example.budgetapp.services.impl;

import com.example.budgetapp.services.BudgetService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;

@Service
public class BudgetServiceImpl implements BudgetService {


    public static final int[] ANNUAL_SALARY = {30_000, 30_000, 30_000, 35_000, 35_000, 35_000, 40_000, 40_000,
            40_000, 45_000, 45_000, 50_000};
    public static int count = ANNUAL_SALARY.length - 1;
    public static final int SALARY = ANNUAL_SALARY[count];
    public static double average = Arrays.stream(ANNUAL_SALARY).average().orElse(Double.NaN);
    public static final double AVG_DAYS = 29.3;

    @Override

    public int getDailyBudget() {
        return SALARY / LocalDate.now().lengthOfMonth();
    }

    @Override
    public int getBalance() {
        return SALARY - (LocalDate.now().getDayOfMonth() * getDailyBudget());
    }

    @Override
    public int getVacationBonus(int daysCount) {
        double avgDaySalary = average / AVG_DAYS;
        return (int) (daysCount * avgDaySalary);
    }

    @Override
    public int getSalaryWithVacation(int vacationDaysCount, int vacationWorkingDaysCount,
                                     int workingDaysInMonth) {
        int salary = SALARY / workingDaysInMonth * (workingDaysInMonth - vacationWorkingDaysCount);
        return salary + getVacationBonus(vacationDaysCount);
    }
}
