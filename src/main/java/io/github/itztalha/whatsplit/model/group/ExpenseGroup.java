package io.github.itztalha.whatsplit.model.group;

import java.util.UUID;

public record ExpenseGroup(
        UUID id,
        String whatsappChatId,
        String name,
        String description,
        String createdByWaId
) {
}
