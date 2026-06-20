package io.github.itztalha.whatsplit.model.group;

import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;

import java.util.UUID;

public record ExpenseGroup(
        UUID id,
        WaChatId whatsappChatId,
        String name,
        String description,
        WaId createdByWaId
) {
}
