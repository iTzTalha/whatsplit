package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.jooq.tables.records.ExpenseParticipantsRecord;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;
import io.github.itztalha.whatsplit.repository.ExpenseParticipantRepository;
import io.github.itztalha.whatsplit.repository.mapper.ExpenseParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.itztalha.whatsplit.jooq.Tables.EXPENSE_PARTICIPANTS;

@Repository
@RequiredArgsConstructor
public class JooqExpenseParticipantRepository implements ExpenseParticipantRepository {

    private final DSLContext dsl;
    private final ExpenseParticipantMapper mapper;

    @Override
    public ExpenseParticipant save(
            ExpenseParticipant expenseParticipant
    ) {
        ExpenseParticipantsRecord record = dsl.newRecord(EXPENSE_PARTICIPANTS);

        record.setExpenseId(expenseParticipant.expenseId());
        record.setWaId(expenseParticipant.waId().value());
        record.setAmount(expenseParticipant.amount());

        record.store();

        return mapper.toModel(record);
    }

    @Override
    public Optional<ExpenseParticipant> findById(
            UUID expenseParticipantId
    ) {
        return dsl.selectFrom(EXPENSE_PARTICIPANTS)
                .where(
                        EXPENSE_PARTICIPANTS.ID.eq(
                                expenseParticipantId
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public Optional<ExpenseParticipant> findByExpenseIdAndWaId(
            UUID expenseId,
            WaId waId
    ) {
        return dsl.selectFrom(EXPENSE_PARTICIPANTS)
                .where(
                        EXPENSE_PARTICIPANTS.EXPENSE_ID.eq(
                                expenseId
                        )
                )
                .and(
                        EXPENSE_PARTICIPANTS.WA_ID.eq(
                                waId.value()
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public List<ExpenseParticipant> findByExpenseId(
            UUID expenseId
    ) {
        return dsl.selectFrom(EXPENSE_PARTICIPANTS)
                .where(
                        EXPENSE_PARTICIPANTS.EXPENSE_ID.eq(
                                expenseId
                        )
                )
                .orderBy(
                        EXPENSE_PARTICIPANTS.WA_ID,
                        EXPENSE_PARTICIPANTS.ID
                )
                .fetch(mapper::toModel);
    }
}