package io.github.itztalha.whatsplit.repository.mapper;

import io.github.itztalha.whatsplit.jooq.tables.records.ExpenseGroupsRecord;
import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import org.springframework.stereotype.Component;

@Component
public class ExpenseGroupMapper {

    public ExpenseGroup toModel(ExpenseGroupsRecord record) {
        return new ExpenseGroup(
                record.getId(),
                new WaChatId(record.getWhatsappChatId()),
                record.getName(),
                record.getDescription(),
                new WaId(record.getCreatedByWaId())
        );
    }
}