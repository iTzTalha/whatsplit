package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseParticipantRepository {

    ExpenseParticipant save(ExpenseParticipant expenseParticipant);

    Optional<ExpenseParticipant> findById(UUID expenseParticipantId);

    Optional<ExpenseParticipant> findByExpenseIdAndWaId( UUID expenseId, WaId waId);

    List<ExpenseParticipant> findByExpenseId(UUID expenseId);
}