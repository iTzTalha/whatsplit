package io.github.itztalha.whatsplit.model.settlement;

import java.math.BigDecimal;
import java.util.UUID;

public record Settlement(
        UUID id,
        UUID expenseGroupId,
        String fromWaId,
        String toWaId,
        BigDecimal amount,
        String currencyCode
) {
}