package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.settlement.Settlement;

import java.util.List;
import java.util.UUID;

public interface SettlementRepository {

    Settlement save(Settlement settlement);

    List<Settlement> findByGroupId(UUID expenseGroupId);
}