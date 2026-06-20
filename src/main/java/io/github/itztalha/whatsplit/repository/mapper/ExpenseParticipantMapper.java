package io.github.itztalha.whatsplit.repository.mapper;

import io.github.itztalha.whatsplit.jooq.tables.records.ExpenseParticipantsRecord;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;
import org.springframework.stereotype.Component;

@Component
public class ExpenseParticipantMapper {

    public ExpenseParticipant toModel(
            ExpenseParticipantsRecord record
    ) {
        return new ExpenseParticipant(
                record.getId(),
                record.getExpenseId(),
                new WaId(record.getWaId()),
                record.getAmount()
        );
    }
}