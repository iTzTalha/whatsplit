package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.settlement.Settlement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository {

    Settlement save(Settlement settlement);

    Optional<Settlement> findById(UUID settlementId);

    List<Settlement> findByGroupId(UUID expenseGroupId);
}