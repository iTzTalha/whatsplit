package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;

import java.util.List;
import java.util.UUID;

public interface ExpenseParticipantRepository {

    List<ExpenseParticipant> saveAll(List<ExpenseParticipant> participants);

    List<ExpenseParticipant> findByExpenseId(UUID expenseId);

    void deleteByExpenseId(UUID expenseId);
}