package io.github.itztalha.whatsplit.service;

import io.github.itztalha.whatsplit.model.balance.ParticipantBalance;
import io.github.itztalha.whatsplit.model.balance.SettlementSuggestion;
import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.expense.Expense;
import io.github.itztalha.whatsplit.model.expense.ExpenseParticipant;
import io.github.itztalha.whatsplit.model.settlement.Settlement;
import io.github.itztalha.whatsplit.repository.ExpenseParticipantRepository;
import io.github.itztalha.whatsplit.repository.ExpenseRepository;
import io.github.itztalha.whatsplit.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultBalanceService implements BalanceService {

    private static final Comparator<WaId> WA_ID_ORDER = Comparator.comparing(WaId::value);

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final SettlementRepository settlementRepository;

    @Override
    public List<ParticipantBalance> calculateBalances(UUID expenseGroupId) {
        return calculateNetAmounts(expenseGroupId).entrySet().stream()
                .flatMap(currencyEntry -> currencyEntry.getValue().entrySet().stream()
                        .filter(participantEntry -> participantEntry.getValue().signum() != 0)
                        .map(participantEntry -> new ParticipantBalance(
                                participantEntry.getKey(),
                                currencyEntry.getKey(),
                                participantEntry.getValue()
                        )))
                .sorted(Comparator.comparing((ParticipantBalance balance) -> balance.currencyCode().value())
                        .thenComparing(ParticipantBalance::waId, WA_ID_ORDER))
                .toList();
    }

    @Override
    public List<SettlementSuggestion> calculateSettlementSuggestions(UUID expenseGroupId) {
        List<SettlementSuggestion> suggestions = new ArrayList<>();

        calculateNetAmounts(expenseGroupId).entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(CurrencyCode::value)))
                .forEach(currencyEntry -> {
            CurrencyCode currencyCode = currencyEntry.getKey();
            Map<WaId, BigDecimal> participantAmounts = currencyEntry.getValue();
            List<Position> positions = participantAmounts.entrySet().stream()
                    .filter(entry -> entry.getValue().signum() != 0)
                    .map(entry -> new Position(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(Position::waId, WA_ID_ORDER))
                    .toList();

            suggestions.addAll(findMinimumSettlementPlan(positions, currencyCode));
        });

        return List.copyOf(suggestions);
    }

    private Map<CurrencyCode, Map<WaId, BigDecimal>> calculateNetAmounts(UUID expenseGroupId) {
        Map<CurrencyCode, Map<WaId, BigDecimal>> netAmountsByCurrency = new HashMap<>();

        for (Expense expense : expenseRepository.findByGroupId(expenseGroupId)) {
            List<ExpenseParticipant> shares = expenseParticipantRepository.findByExpenseId(expense.id());
            BigDecimal totalShares = shares.stream()
                    .map(ExpenseParticipant::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalShares.compareTo(expense.amount()) != 0) {
                throw new IllegalStateException("Expense shares must equal the expense amount: " + expense.id());
            }

            add(netAmountsByCurrency, expense.currencyCode(), expense.paidByWaId(), expense.amount());
            for (ExpenseParticipant share : shares) {
                add(netAmountsByCurrency, expense.currencyCode(), share.waId(), share.amount().negate());
            }
        }

        for (Settlement settlement : settlementRepository.findByGroupId(expenseGroupId)) {
            add(netAmountsByCurrency, settlement.currencyCode(), settlement.fromWaId(), settlement.amount());
            add(netAmountsByCurrency, settlement.currencyCode(), settlement.toWaId(), settlement.amount().negate());
        }

        return netAmountsByCurrency;
    }

    private void add(
            Map<CurrencyCode, Map<WaId, BigDecimal>> netAmountsByCurrency,
            CurrencyCode currencyCode,
            WaId waId,
            BigDecimal amount
    ) {
        netAmountsByCurrency
                .computeIfAbsent(currencyCode, ignored -> new HashMap<>())
                .merge(waId, amount, BigDecimal::add);
    }

    private List<SettlementSuggestion> findMinimumSettlementPlan(
            List<Position> positions,
            CurrencyCode currencyCode
    ) {
        int sourceIndex = firstUnsettledPosition(positions);
        if (sourceIndex == -1) {
            return List.of();
        }

        Position source = positions.get(sourceIndex);
        List<SettlementSuggestion> bestPlan = null;

        for (int targetIndex = sourceIndex + 1; targetIndex < positions.size(); targetIndex++) {
            Position target = positions.get(targetIndex);
            if (source.amount().signum() == target.amount().signum() || target.amount().signum() == 0) {
                continue;
            }

            BigDecimal amount = source.amount().abs().min(target.amount().abs());
            SettlementSuggestion suggestion = source.amount().signum() < 0
                    ? new SettlementSuggestion(source.waId(), target.waId(), currencyCode, amount)
                    : new SettlementSuggestion(target.waId(), source.waId(), currencyCode, amount);

            List<Position> nextPositions = new ArrayList<>(positions);
            nextPositions.set(sourceIndex, new Position(source.waId(), apply(source.amount(), amount)));
            nextPositions.set(targetIndex, new Position(target.waId(), apply(target.amount(), amount)));

            List<SettlementSuggestion> candidatePlan = new ArrayList<>();
            candidatePlan.add(suggestion);
            candidatePlan.addAll(findMinimumSettlementPlan(nextPositions, currencyCode));

            if (isBetterPlan(candidatePlan, bestPlan)) {
                bestPlan = candidatePlan;
            }
        }

        if (bestPlan == null) {
            throw new IllegalStateException("Balances do not reconcile for currency " + currencyCode);
        }

        return bestPlan;
    }

    private int firstUnsettledPosition(List<Position> positions) {
        for (int index = 0; index < positions.size(); index++) {
            if (positions.get(index).amount().signum() != 0) {
                return index;
            }
        }
        return -1;
    }

    private BigDecimal apply(BigDecimal currentAmount, BigDecimal settlementAmount) {
        return currentAmount.signum() < 0
                ? currentAmount.add(settlementAmount)
                : currentAmount.subtract(settlementAmount);
    }

    private boolean isBetterPlan(
            List<SettlementSuggestion> candidatePlan,
            List<SettlementSuggestion> currentBestPlan
    ) {
        if (currentBestPlan == null || candidatePlan.size() < currentBestPlan.size()) {
            return true;
        }
        if (candidatePlan.size() > currentBestPlan.size()) {
            return false;
        }

        return planKey(candidatePlan).compareTo(planKey(currentBestPlan)) < 0;
    }

    private String planKey(List<SettlementSuggestion> plan) {
        return plan.stream()
                .map(suggestion -> suggestion.fromWaId().value()
                        + "|" + suggestion.toWaId().value()
                        + "|" + suggestion.amount())
                .reduce("", (left, right) -> left + "\n" + right);
    }

    private record Position(WaId waId, BigDecimal amount) {
    }
}
