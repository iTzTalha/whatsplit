package io.github.itztalha.whatsplit.model.settlement;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;

import java.math.BigDecimal;
import java.util.UUID;

public record Settlement(
        UUID id,
        UUID expenseGroupId,
        WaId fromWaId,
        WaId toWaId,
        BigDecimal amount,
        CurrencyCode currencyCode
) {
}