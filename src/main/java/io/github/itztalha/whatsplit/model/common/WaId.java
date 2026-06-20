package io.github.itztalha.whatsplit.model.common;

import java.util.Objects;

public record WaId(String value) {

    public WaId {
        Objects.requireNonNull(value, "WhatsApp ID cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("WhatsApp ID cannot be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}