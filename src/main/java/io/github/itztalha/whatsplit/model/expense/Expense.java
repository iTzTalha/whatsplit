package io.github.itztalha.whatsplit.model.expense;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;

import java.math.BigDecimal;
import java.util.UUID;

public record Expense(
        UUID id,
        UUID expenseGroupId,
        WaId paidByWaId,
        String description,
        BigDecimal amount,
        CurrencyCode currencyCode,
        SplitType splitType
) {
}