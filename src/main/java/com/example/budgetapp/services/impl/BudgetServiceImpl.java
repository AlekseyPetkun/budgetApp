package com.example.budgetapp.services.impl;

import com.example.budgetapp.model.Category;
import com.example.budgetapp.model.Transaction;
import com.example.budgetapp.services.BudgetService;
import com.example.budgetapp.services.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class BudgetServiceImpl implements BudgetService {

    final private FileService fileService;

    public BudgetServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    public static final int[] ANNUAL_SALARY = {30_000, 30_000, 30_000, 35_000, 35_000, 35_000, 40_000, 40_000,
            40_000, 45_000, 45_000, 50_000};
    public static int count = ANNUAL_SALARY.length - 1;
    public static final int SALARY = ANNUAL_SALARY[count];
    public static final int SAVING = 3_000;
    public static final int DAILY_BUDGET = (SALARY - SAVING) / LocalDate.now().lengthOfMonth();
    public static int balance = 0;
    public static double average = Arrays.stream(ANNUAL_SALARY).average().orElse(Double.NaN);
    public static final double AVG_DAYS = 29.3;
    private static TreeMap<Month, LinkedHashMap<Long, Transaction>> transactions = new TreeMap<>();
    private static long lastId = 0;

    @PostConstruct
    private void init() {
        try {
            readFromFile();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override

    public int getDailyBudget() {
        return DAILY_BUDGET;
    }

    @Override
    public int getBalance() {
//        return SALARY - (LocalDate.now().getDayOfMonth() * getDailyBudget());
        return SALARY - SAVING - getAllSpend();
    }

    @Override
    public long addTransaction(Transaction transaction) {
        LinkedHashMap<Long, Transaction> monthTransactions;
        monthTransactions = transactions.getOrDefault(LocalDate.now().getMonth(), new LinkedHashMap<>());
        monthTransactions.put(lastId, transaction);
        transactions.put(LocalDate.now().getMonth(), monthTransactions);
        saveToFile();
        return lastId++;
    }

    @Override
    public Transaction getTransaction(long lastId) {
        for (Map<Long, Transaction> transactionsByMonth : transactions.values()) {
            Transaction transaction = transactionsByMonth.get(lastId);
            if (transaction != null) {
                return transaction;
            }
        }
        return null;
    }

    @Override
    public Transaction editTransaction(long lastId, Transaction transaction) {
        for (Map<Long, Transaction> transactionsByMonth : transactions.values()) {
            if (transactionsByMonth.containsKey(lastId)) {
                transactionsByMonth.put(lastId, transaction);
                saveToFile();
                return transaction;
            }
        }
        return null;
    }

    @Override
    public boolean deleteTransaction(long lastId) {
        for (Map<Long, Transaction> transactionsByMonth : transactions.values()) {
            if (transactionsByMonth.containsKey(lastId)) {
                transactionsByMonth.remove(lastId);
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAllTransaction() {
        transactions = new TreeMap<>();
    }

    @Override
    public int getDailyBalance() {
        return DAILY_BUDGET * LocalDate.now().getDayOfMonth() - getAllSpend();
    }

    @Override
    public int getAllSpend() {
        Map<Long, Transaction> monthTransactions;
        monthTransactions = transactions.getOrDefault(LocalDate.now().getMonth(), new LinkedHashMap<>());
        int sum = 0;
        for (Transaction transaction : monthTransactions.values()) {
            sum += transaction.getSum();
        }
        return sum;
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

    @Override
    public Path createMonthlyReport(Month month) throws IOException {
        LinkedHashMap<Long, Transaction> monthlyTransactions =
                transactions.getOrDefault(month, new LinkedHashMap<>());
        Path path = fileService.createTempFile("monthlyReport");
        for (Transaction transaction : monthlyTransactions.values()) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append(transaction.getCategory().getTranslation() + ": " + transaction.getSum() +
                        " руб. ......................." + transaction.getComment());
                writer.append("\n");//почему-то не выводится текст с новой строки...
            }
        }
        return path;
    }

    @Override
    public void addTransactionsFromInputStream(InputStream inputStream) { //
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) { //Преобразуем байтовые потоки в символьные
            String line;
            while ((line = reader.readLine()) != null) {
                String[] array = StringUtils.split(line, '|');
                Transaction transaction = new Transaction(Category.valueOf(array[0]), Integer.valueOf(array[1]), array[2]);
                addTransaction(transaction);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void saveToFile() {
        try {
            DataFile dataFile = new DataFile(lastId+1, transactions);
            String json = new ObjectMapper().writeValueAsString(dataFile);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try {
            String json = fileService.readFromFile();
            DataFile dataFile = new ObjectMapper().readValue(json, new TypeReference<DataFile>() {
            });
            lastId = dataFile.getLastId();
            transactions = dataFile.getTransactions();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DataFile {
        private long lastId;
        private TreeMap<Month, LinkedHashMap<Long, Transaction>> transactions;
    }

}
