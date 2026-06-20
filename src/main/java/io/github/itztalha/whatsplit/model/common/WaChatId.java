package io.github.itztalha.whatsplit.model.common;

import java.util.Objects;

public record WaChatId(String value) {

    public WaChatId {
        Objects.requireNonNull(value, "WhatsApp chat ID cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "WhatsApp chat ID cannot be blank"
            );
        }
    }

    @Override
    public String toString() {
        return value;
    }
}