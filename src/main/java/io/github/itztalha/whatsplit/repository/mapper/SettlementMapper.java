package io.github.itztalha.whatsplit.repository.mapper;

import io.github.itztalha.whatsplit.jooq.tables.records.SettlementsRecord;
import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.settlement.Settlement;
import org.springframework.stereotype.Component;

@Component
public class SettlementMapper {

    public Settlement toModel(SettlementsRecord record) {
        return new Settlement(
                record.getId(),
                record.getExpenseGroupId(),
                new WaId(record.getFromWaId()),
                new WaId(record.getToWaId()),
                record.getAmount(),
                new CurrencyCode(record.getCurrencyCode())
        );
    }
}