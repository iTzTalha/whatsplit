package io.github.itztalha.whatsplit.model.common;

import java.util.Locale;
import java.util.Objects;

public record CurrencyCode(String value) {

    public CurrencyCode {
        Objects.requireNonNull(value, "Currency code cannot be null");

        value = value.trim().toUpperCase(Locale.ROOT);

        if (value.length() != 3) {
            throw new IllegalArgumentException(
                    "Currency code must be exactly 3 characters"
            );
        }
    }

    @Override
    public String toString() {
        return value;
    }
}