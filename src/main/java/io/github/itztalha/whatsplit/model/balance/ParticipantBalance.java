package io.github.itztalha.whatsplit.model.balance;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;

import java.math.BigDecimal;
import java.util.Objects;

public record ParticipantBalance(
        WaId waId,
        CurrencyCode currencyCode,
        BigDecimal amount
) {

    public ParticipantBalance {
        Objects.requireNonNull(waId, "WhatsApp ID cannot be null");
        Objects.requireNonNull(currencyCode, "Currency code cannot be null");
        Objects.requireNonNull(amount, "Balance amount cannot be null");
    }
}
