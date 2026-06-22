package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.jooq.tables.records.SettlementsRecord;
import io.github.itztalha.whatsplit.model.settlement.Settlement;
import io.github.itztalha.whatsplit.repository.SettlementRepository;
import io.github.itztalha.whatsplit.repository.mapper.SettlementMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.itztalha.whatsplit.jooq.Tables.SETTLEMENTS;

@Repository
@RequiredArgsConstructor
public class JooqSettlementRepository implements SettlementRepository {

    private final DSLContext dsl;
    private final SettlementMapper mapper;

    @Override
    public Settlement save(
            Settlement settlement
    ) {
        SettlementsRecord record = dsl.newRecord(SETTLEMENTS);

        record.setExpenseGroupId(settlement.expenseGroupId());
        record.setFromWaId(settlement.fromWaId().value());
        record.setToWaId(settlement.toWaId().value());
        record.setAmount(settlement.amount());
        record.setCurrencyCode(settlement.currencyCode().value());

        record.store();

        return mapper.toModel(record);
    }

    @Override
    public Optional<Settlement> findById(
            UUID settlementId
    ) {
        return dsl.selectFrom(SETTLEMENTS)
                .where(
                        SETTLEMENTS.ID.eq(
                                settlementId
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public List<Settlement> findByGroupId(
            UUID expenseGroupId
    ) {
        return dsl.selectFrom(SETTLEMENTS)
                .where(
                        SETTLEMENTS.EXPENSE_GROUP_ID.eq(
                                expenseGroupId
                        )
                )
                .orderBy(
                        SETTLEMENTS.ID
                )
                .fetch(mapper::toModel);
    }
}