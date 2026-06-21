package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.jooq.tables.records.ExpensesRecord;
import io.github.itztalha.whatsplit.model.expense.Expense;
import io.github.itztalha.whatsplit.repository.ExpenseRepository;
import io.github.itztalha.whatsplit.repository.mapper.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.itztalha.whatsplit.jooq.Tables.EXPENSES;

@Repository
@RequiredArgsConstructor
public class JooqExpenseRepository implements ExpenseRepository {

    private final DSLContext dsl;
    private final ExpenseMapper mapper;

    @Override
    public Expense save(
            Expense expense
    ) {
        ExpensesRecord record = dsl.newRecord(EXPENSES);

        record.setExpenseGroupId(expense.expenseGroupId());
        record.setPaidByWaId(expense.paidByWaId().value());
        record.setDescription(expense.description());
        record.setAmount(expense.amount());
        record.setCurrencyCode(expense.currencyCode().value());
        record.setSplitType(expense.splitType().name());

        record.store();

        return mapper.toModel(record);
    }

    @Override
    public Optional<Expense> findById(
            UUID expenseId
    ) {
        return dsl.selectFrom(EXPENSES)
                .where(
                        EXPENSES.ID.eq(
                                expenseId
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public List<Expense> findByGroupId(
            UUID expenseGroupId
    ) {
        return dsl.selectFrom(EXPENSES)
                .where(
                        EXPENSES.EXPENSE_GROUP_ID.eq(
                                expenseGroupId
                        )
                )
                .orderBy(
                        EXPENSES.ID
                )
                .fetch(mapper::toModel);
    }

    @Override
    public void deleteById(
            UUID expenseId
    ) {
        dsl.deleteFrom(EXPENSES)
                .where(
                        EXPENSES.ID.eq(
                                expenseId
                        )
                )
                .execute();
    }
}