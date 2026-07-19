package io.github.itztalha.whatsplit.service;

import io.github.itztalha.whatsplit.model.balance.ParticipantBalance;
import io.github.itztalha.whatsplit.model.balance.SettlementSuggestion;

import java.util.List;
import java.util.UUID;

public interface BalanceService {

    List<ParticipantBalance> calculateBalances(UUID expenseGroupId);

    List<SettlementSuggestion> calculateSettlementSuggestions(UUID expenseGroupId);
}
