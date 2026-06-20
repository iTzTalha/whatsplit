package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseGroupRepository {

    ExpenseGroup save(ExpenseGroup expenseGroup);

    Optional<ExpenseGroup> findById(UUID expenseGroupId);

    List<ExpenseGroup> findByChatId(WaChatId chatId);

    boolean existsByChatIdAndName(WaChatId chatId, String name);

    void deleteById(UUID expenseGroupId);
}