package io.github.itztalha.whatsplit.service;

import io.github.itztalha.whatsplit.model.balance.ParticipantBalance;
import io.github.itztalha.whatsplit.model.balance.SettlementSuggestion;
import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.Expense;
import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;
import io.github.itztalha.whatsplit.model.expense.SplitType;
import io.github.itztalha.whatsplit.model.settlement.Settlement;
import io.github.itztalha.whatsplit.repository.ExpenseParticipantRepository;
import io.github.itztalha.whatsplit.repository.ExpenseRepository;
import io.github.itztalha.whatsplit.repository.SettlementRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultBalanceServiceTest {

    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final CurrencyCode INR = new CurrencyCode("INR");
    private static final WaId ALICE = new WaId("alice");
    private static final WaId BOB = new WaId("bob");
    private static final WaId CHARLIE = new WaId("charlie");

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final ExpenseParticipantRepository expenseParticipantRepository = mock(ExpenseParticipantRepository.class);
    private final SettlementRepository settlementRepository = mock(SettlementRepository.class);
    private final BalanceService balanceService = new DefaultBalanceService(
            expenseRepository, expenseParticipantRepository, settlementRepository
    );

    @Test
    void shouldCalculateBalancesAndApplySettlements() {
        Expense expense = expense(ALICE, "1200.00");
        when(expenseRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(expense));
        when(expenseParticipantRepository.findByExpenseId(expense.id())).thenReturn(List.of(
                share(expense.id(), ALICE, "400.00"),
                share(expense.id(), BOB, "400.00"),
                share(expense.id(), CHARLIE, "400.00")
        ));
        when(settlementRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(
                new Settlement(UUID.randomUUID(), GROUP_ID, BOB, ALICE, new BigDecimal("100.00"), INR)
        ));

        assertThat(balanceService.calculateBalances(GROUP_ID)).containsExactly(
                new ParticipantBalance(ALICE, INR, new BigDecimal("700.00")),
                new ParticipantBalance(BOB, INR, new BigDecimal("-300.00")),
                new ParticipantBalance(CHARLIE, INR, new BigDecimal("-400.00"))
        );
    }

    @Test
    void shouldProduceMinimumSettlementSuggestions() {
        Expense expense = expense(ALICE, "1200.00");
        when(expenseRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(expense));
        when(expenseParticipantRepository.findByExpenseId(expense.id())).thenReturn(List.of(
                share(expense.id(), ALICE, "300.00"),
                share(expense.id(), BOB, "200.00"),
                share(expense.id(), CHARLIE, "700.00")
        ));
        when(settlementRepository.findByGroupId(GROUP_ID)).thenReturn(List.of());

        assertThat(balanceService.calculateSettlementSuggestions(GROUP_ID)).containsExactly(
                new SettlementSuggestion(BOB, ALICE, INR, new BigDecimal("200.00")),
                new SettlementSuggestion(CHARLIE, ALICE, INR, new BigDecimal("700.00"))
        );
    }

    @Test
    void shouldKeepCurrenciesSeparate() {
        Expense inrExpense = expense(ALICE, "100.00");
        Expense usdExpense = new Expense(
                UUID.randomUUID(), GROUP_ID, BOB, "Taxi", new BigDecimal("20.00"), new CurrencyCode("USD"), SplitType.EXACT_AMOUNT
        );
        when(expenseRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(inrExpense, usdExpense));
        when(expenseParticipantRepository.findByExpenseId(inrExpense.id())).thenReturn(List.of(
                share(inrExpense.id(), ALICE, "50.00"), share(inrExpense.id(), BOB, "50.00")
        ));
        when(expenseParticipantRepository.findByExpenseId(usdExpense.id())).thenReturn(List.of(
                share(usdExpense.id(), ALICE, "10.00"), share(usdExpense.id(), BOB, "10.00")
        ));
        when(settlementRepository.findByGroupId(GROUP_ID)).thenReturn(List.of());

        assertThat(balanceService.calculateSettlementSuggestions(GROUP_ID)).containsExactly(
                new SettlementSuggestion(BOB, ALICE, INR, new BigDecimal("50.00")),
                new SettlementSuggestion(ALICE, BOB, new CurrencyCode("USD"), new BigDecimal("10.00"))
        );
    }

    @Test
    void shouldFindThePlanWithTheFewestTransactions() {
        WaId dave = new WaId("dave");
        Expense aliceExpense = expense(ALICE, "6.00");
        Expense bobExpense = new Expense(
                UUID.randomUUID(), GROUP_ID, BOB, "Dinner", new BigDecimal("4.00"), INR, SplitType.EXACT_AMOUNT
        );
        when(expenseRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(aliceExpense, bobExpense));
        when(expenseParticipantRepository.findByExpenseId(aliceExpense.id())).thenReturn(List.of(
                share(aliceExpense.id(), CHARLIE, "4.00"), share(aliceExpense.id(), dave, "2.00")
        ));
        when(expenseParticipantRepository.findByExpenseId(bobExpense.id())).thenReturn(List.of(
                share(bobExpense.id(), dave, "4.00")
        ));
        when(settlementRepository.findByGroupId(GROUP_ID)).thenReturn(List.of());

        assertThat(balanceService.calculateSettlementSuggestions(GROUP_ID)).containsExactlyInAnyOrder(
                new SettlementSuggestion(dave, ALICE, INR, new BigDecimal("6.00")),
                new SettlementSuggestion(CHARLIE, BOB, INR, new BigDecimal("4.00"))
        );
    }

    @Test
    void shouldRejectExpenseWithUnreconciledShares() {
        Expense expense = expense(ALICE, "100.00");
        when(expenseRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(expense));
        when(expenseParticipantRepository.findByExpenseId(expense.id())).thenReturn(List.of(
                share(expense.id(), ALICE, "40.00"), share(expense.id(), BOB, "40.00")
        ));

        assertThatThrownBy(() -> balanceService.calculateBalances(GROUP_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Expense shares must equal the expense amount");
    }

    private Expense expense(WaId paidBy, String amount) {
        return new Expense(
                UUID.randomUUID(), GROUP_ID, paidBy, "Trip expense", new BigDecimal(amount), INR, SplitType.EQUAL
        );
    }

    private ExpenseParticipant share(UUID expenseId, WaId waId, String amount) {
        return new ExpenseParticipant(UUID.randomUUID(), expenseId, waId, new BigDecimal(amount));
    }
}
