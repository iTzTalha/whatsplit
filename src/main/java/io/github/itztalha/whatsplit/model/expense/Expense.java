package io.github.itztalha.whatsplit.model.expense;

import java.math.BigDecimal;
import java.util.UUID;

public record Expense(
        UUID id,
        UUID expenseGroupId,
        String paidByWaId,
        String description,
        BigDecimal amount,
        String currencyCode,
        SplitType splitType
) {
}