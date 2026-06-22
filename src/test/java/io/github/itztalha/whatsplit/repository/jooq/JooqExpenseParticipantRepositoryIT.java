package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.Expense;
import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;
import io.github.itztalha.whatsplit.model.expense.SplitType;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import io.github.itztalha.whatsplit.repository.ExpenseGroupRepository;
import io.github.itztalha.whatsplit.repository.ExpenseParticipantRepository;
import io.github.itztalha.whatsplit.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JooqExpenseParticipantRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private ExpenseParticipantRepository repository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Test
    void shouldSaveExpenseParticipant() {
        Expense expense = createExpense();

        ExpenseParticipant participant =
                new ExpenseParticipant(
                        null,
                        expense.id(),
                        new WaId("919999999999"),
                        new BigDecimal("400.00")
                );

        ExpenseParticipant saved =
                repository.save(participant);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.expenseId())
                .isEqualTo(expense.id());
        assertThat(saved.waId())
                .isEqualTo(new WaId("919999999999"));
        assertThat(saved.amount())
                .isEqualByComparingTo("400.00");
    }

    @Test
    void shouldFindExpenseParticipantById() {
        Expense expense = createExpense();

        ExpenseParticipant saved =
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("919999999999"),
                                new BigDecimal("400.00")
                        )
                );

        Optional<ExpenseParticipant> found =
                repository.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get())
                .isEqualTo(saved);
    }

    @Test
    void shouldReturnEmptyWhenExpenseParticipantDoesNotExist() {
        assertThat(
                repository.findById(UUID.randomUUID())
        ).isEmpty();
    }

    @Test
    void shouldFindExpenseParticipantByExpenseIdAndWaId() {
        Expense expense = createExpense();

        ExpenseParticipant saved =
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("919999999999"),
                                new BigDecimal("400.00")
                        )
                );

        Optional<ExpenseParticipant> found =
                repository.findByExpenseIdAndWaId(
                        expense.id(),
                        new WaId("919999999999")
                );

        assertThat(found).isPresent();
        assertThat(found.get())
                .isEqualTo(saved);
    }

    @Test
    void shouldReturnEmptyWhenExpenseParticipantDoesNotExistForExpenseAndWaId() {
        Expense expense = createExpense();

        assertThat(
                repository.findByExpenseIdAndWaId(
                        expense.id(),
                        new WaId("911111111111")
                )
        ).isEmpty();
    }

    @Test
    void shouldFindExpenseParticipantsByExpenseId() {
        Expense expense = createExpense();

        ExpenseParticipant first =
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("919999999999"),
                                new BigDecimal("400.00")
                        )
                );

        ExpenseParticipant second =
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("918888888888"),
                                new BigDecimal("800.00")
                        )
                );

        Expense anotherExpense =
                createExpense();

        repository.save(
                new ExpenseParticipant(
                        null,
                        anotherExpense.id(),
                        new WaId("917777777777"),
                        new BigDecimal("200.00")
                )
        );

        assertThat(
                repository.findByExpenseId(
                        expense.id()
                )
        )
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        first,
                        second
                );
    }

    @Test
    void shouldRejectExpenseParticipantWithNonPositiveAmount() {
        Expense expense = createExpense();

        assertThatThrownBy(() ->
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("919999999999"),
                                BigDecimal.ZERO
                        )
                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                )
                .hasMessageContaining(
                        "chk_expense_participants_amount_positive"
                );
    }

    @Test
    void shouldRejectDuplicateExpenseParticipant() {
        Expense expense = createExpense();

        repository.save(
                new ExpenseParticipant(
                        null,
                        expense.id(),
                        new WaId("919999999999"),
                        new BigDecimal("400.00")
                )
        );

        assertThatThrownBy(() ->
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("919999999999"),
                                new BigDecimal("500.00")
                        )
                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                )
                .hasMessageContaining(
                        "uq_expense_participant"
                );
    }

    @Test
    void shouldReturnEmptyListWhenExpenseHasNoParticipants() {
        Expense expense = createExpense();

        assertThat(
                repository.findByExpenseId(
                        expense.id()
                )
        ).isEmpty();
    }

    @Test
    void shouldDeleteExpenseParticipantsWhenExpenseIsDeleted() {
        Expense expense = createExpense();

        ExpenseParticipant participant =
                repository.save(
                        new ExpenseParticipant(
                                null,
                                expense.id(),
                                new WaId("919999999999"),
                                new BigDecimal("400.00")
                        )
                );

        expenseRepository.deleteById(
                expense.id()
        );

        assertThat(
                repository.findById(
                        participant.id()
                )
        ).isEmpty();
    }

    private Expense createExpense() {
        ExpenseGroup group =
                expenseGroupRepository.save(
                        new ExpenseGroup(
                                null,
                                new WaChatId("120363123456789@g.us"),
                                "Group-" + UUID.randomUUID(),
                                null,
                                new WaId("919999999999")
                        )
                );

        return expenseRepository.save(
                new Expense(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        "Hotel",
                        new BigDecimal("1200.00"),
                        new CurrencyCode("INR"),
                        SplitType.EQUAL
                )
        );
    }
}