package io.github.itztalha.whatsplit.model.balance;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;

import java.math.BigDecimal;
import java.util.Objects;

public record SettlementSuggestion(
        WaId fromWaId,
        WaId toWaId,
        CurrencyCode currencyCode,
        BigDecimal amount
) {

    public SettlementSuggestion {
        Objects.requireNonNull(fromWaId, "Payer WhatsApp ID cannot be null");
        Objects.requireNonNull(toWaId, "Receiver WhatsApp ID cannot be null");
        Objects.requireNonNull(currencyCode, "Currency code cannot be null");
        Objects.requireNonNull(amount, "Settlement amount cannot be null");

        if (fromWaId.equals(toWaId)) {
            throw new IllegalArgumentException("Settlement parties must differ");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Settlement amount must be positive");
        }
    }
}
