package io.github.itztalha.whatsplit.model.group;

import java.util.UUID;

public record GroupParticipant(
        UUID id,
        UUID expenseGroupId,
        String waId,
        boolean active
) {
}