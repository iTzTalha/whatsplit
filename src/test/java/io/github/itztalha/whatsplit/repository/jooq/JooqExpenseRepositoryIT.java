package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.Expense;
import io.github.itztalha.whatsplit.model.expense.SplitType;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import io.github.itztalha.whatsplit.repository.ExpenseGroupRepository;
import io.github.itztalha.whatsplit.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JooqExpenseRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Test
    void shouldSaveExpense() {
        ExpenseGroup group = createExpenseGroup();

        Expense expense = new Expense(
                null,
                group.id(),
                new WaId("919999999999"),
                "Dinner",
                new BigDecimal("1000.00"),
                new CurrencyCode("INR"),
                SplitType.EQUAL
        );

        Expense saved = expenseRepository.save(expense);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.expenseGroupId()).isEqualTo(group.id());
        assertThat(saved.paidByWaId())
                .isEqualTo(new WaId("919999999999"));
        assertThat(saved.description())
                .isEqualTo("Dinner");
        assertThat(saved.amount())
                .isEqualByComparingTo("1000.00");
        assertThat(saved.currencyCode())
                .isEqualTo(new CurrencyCode("INR"));
        assertThat(saved.splitType())
                .isEqualTo(SplitType.EQUAL);
    }

    @Test
    void shouldFindExpenseById() {
        ExpenseGroup group = createExpenseGroup();

        Expense saved = expenseRepository.save(
                new Expense(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        "Taxi",
                        new BigDecimal("500.00"),
                        new CurrencyCode("INR"),
                        SplitType.EQUAL
                )
        );

        Optional<Expense> found =
                expenseRepository.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    void shouldReturnEmptyWhenExpenseDoesNotExist() {
        Optional<Expense> found =
                expenseRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindExpensesByExpenseGroupId() {
        ExpenseGroup group = createExpenseGroup();

        Expense first = expenseRepository.save(
                new Expense(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        "Dinner",
                        new BigDecimal("1000.00"),
                        new CurrencyCode("INR"),
                        SplitType.EQUAL
                )
        );

        Expense second = expenseRepository.save(
                new Expense(
                        null,
                        group.id(),
                        new WaId("918888888888"),
                        "Taxi",
                        new BigDecimal("500.00"),
                        new CurrencyCode("INR"),
                        SplitType.EQUAL
                )
        );

        ExpenseGroup otherGroup =
                createAnotherExpenseGroup();

        expenseRepository.save(
                new Expense(
                        null,
                        otherGroup.id(),
                        new WaId("917777777777"),
                        "Hotel",
                        new BigDecimal("2000.00"),
                        new CurrencyCode("INR"),
                        SplitType.EQUAL
                )
        );

        List<Expense> expenses =
                expenseRepository.findByGroupId(group.id());

        assertThat(expenses)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        first,
                        second
                );
    }

    @Test
    void shouldDeleteExpense() {
        ExpenseGroup group = createExpenseGroup();

        Expense saved = expenseRepository.save(
                new Expense(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        "Delete Me",
                        new BigDecimal("100.00"),
                        new CurrencyCode("INR"),
                        SplitType.EQUAL
                )
        );

        expenseRepository.deleteById(saved.id());

        assertThat(
                expenseRepository.findById(saved.id())
        ).isEmpty();
    }

    @Test
    void shouldRejectExpenseWithNonPositiveAmount() {
        ExpenseGroup group = createExpenseGroup();

        assertThatThrownBy(() ->
                expenseRepository.save(
                        new Expense(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                "Invalid Expense",
                                BigDecimal.ZERO,
                                new CurrencyCode("INR"),
                                SplitType.EQUAL
                        )
                )
        ).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    private ExpenseGroup createExpenseGroup() {
        return expenseGroupRepository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363123456789@g.us"),
                        "Goa Trip",
                        null,
                        new WaId("919999999999")
                )
        );
    }

    private ExpenseGroup createAnotherExpenseGroup() {
        return expenseGroupRepository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363987654321@g.us"),
                        "Europe Trip",
                        null,
                        new WaId("918888888888")
                )
        );
    }
}