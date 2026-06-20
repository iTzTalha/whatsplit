package io.github.itztalha.whatsplit.model.group;

import io.github.itztalha.whatsplit.model.common.WaId;

import java.util.UUID;

public record GroupParticipant(
        UUID id,
        UUID expenseGroupId,
        WaId waId,
        boolean active
) {
}