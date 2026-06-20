package io.github.itztalha.whatsplit.model.expense;

import io.github.itztalha.whatsplit.model.common.WaId;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseParticipant(
        UUID id,
        UUID expenseId,
        WaId waId,
        BigDecimal amount
) {
}