package io.github.itztalha.whatsplit.repository.mapper;

import io.github.itztalha.whatsplit.jooq.tables.records.ExpensesRecord;
import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.Expense;
import io.github.itztalha.whatsplit.model.expense.SplitType;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public Expense toModel(ExpensesRecord record) {
        return new Expense(
                record.getId(),
                record.getExpenseGroupId(),
                new WaId(record.getPaidByWaId()),
                record.getDescription(),
                record.getAmount(),
                new CurrencyCode(record.getCurrencyCode()),
                SplitType.valueOf(record.getSplitType())
        );
    }
}