package com.example.budgetapp.services;

import com.example.budgetapp.model.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Month;

public interface BudgetService {

    int getDailyBudget();

    int getBalance();

    long addTransaction(Transaction transaction);

    Transaction getTransaction(long lastId);

    Transaction editTransaction(long lastId, Transaction transaction);


    boolean deleteTransaction(long lastId);

    void deleteAllTransaction();

    int getDailyBalance();

    int getAllSpend();

    int getVacationBonus(int daysCount);

    int getSalaryWithVacation(int vacationDaysCount, int vacationWorkingDaysCount, int workingDaysInMonth);

    Path createMonthlyReport(Month month) throws IOException;

    void addTransactionsFromInputStream(InputStream inputStream);
}
