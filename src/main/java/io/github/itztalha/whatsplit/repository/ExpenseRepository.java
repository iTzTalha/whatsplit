package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.expense.Expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findById(UUID expenseId);

    List<Expense> findByGroupId(UUID expenseGroupId);

    void deleteById(UUID expenseId);
}