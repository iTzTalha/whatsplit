package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import io.github.itztalha.whatsplit.repository.ExpenseGroupRepository;
import io.github.itztalha.whatsplit.repository.mapper.ExpenseGroupMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.itztalha.whatsplit.jooq.Tables.EXPENSE_GROUPS;

@Repository
@RequiredArgsConstructor
public class JooqExpenseGroupRepository implements ExpenseGroupRepository {

    private final DSLContext dsl;
    private final ExpenseGroupMapper mapper;

    @Override
    public ExpenseGroup save(
            ExpenseGroup expenseGroup
    ) {
        return dsl.insertInto(EXPENSE_GROUPS)
                .set(
                        EXPENSE_GROUPS.WHATSAPP_CHAT_ID,
                        expenseGroup.chatId().value()
                )
                .set(
                        EXPENSE_GROUPS.NAME,
                        expenseGroup.name()
                )
                .set(
                        EXPENSE_GROUPS.DESCRIPTION,
                        expenseGroup.description()
                )
                .set(
                        EXPENSE_GROUPS.CREATED_BY_WA_ID,
                        expenseGroup.createdByWaId().value()
                )
                .returning()
                .fetchOptional()
                .map(mapper::toModel)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Failed to persist expense group"
                        )
                );
    }

    @Override
    public Optional<ExpenseGroup> findById(
            UUID expenseGroupId
    ) {
        return dsl.selectFrom(EXPENSE_GROUPS)
                .where(
                        EXPENSE_GROUPS.ID.eq(
                                expenseGroupId
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public List<ExpenseGroup> findByChatId(
            WaChatId chatId
    ) {
        return dsl.selectFrom(EXPENSE_GROUPS)
                .where(
                        EXPENSE_GROUPS.WHATSAPP_CHAT_ID.eq(
                                chatId.value()
                        )
                )
                .orderBy(
                        EXPENSE_GROUPS.NAME,
                        EXPENSE_GROUPS.ID
                )
                .fetch(mapper::toModel);
    }

    @Override
    public boolean existsByChatIdAndName(
            WaChatId chatId,
            String name
    ) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(EXPENSE_GROUPS)
                        .where(
                                EXPENSE_GROUPS.WHATSAPP_CHAT_ID.eq(
                                        chatId.value()
                                )
                        )
                        .and(
                                EXPENSE_GROUPS.NAME.eq(
                                        name
                                )
                        )
        );
    }

    @Override
    public void deleteById(
            UUID expenseGroupId
    ) {
        dsl.deleteFrom(EXPENSE_GROUPS)
                .where(
                        EXPENSE_GROUPS.ID.eq(
                                expenseGroupId
                        )
                )
                .execute();
    }
}