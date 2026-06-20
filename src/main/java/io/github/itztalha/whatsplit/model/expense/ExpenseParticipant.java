package io.github.itztalha.whatsplit.model.expense;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseParticipant(
        UUID id,
        UUID expenseId,
        String waId,
        BigDecimal amount
) {
}