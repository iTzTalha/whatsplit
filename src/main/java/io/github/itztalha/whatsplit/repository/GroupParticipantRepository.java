package io.github.itztalha.whatsplit.repository;

import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.GroupParticipant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupParticipantRepository {

    GroupParticipant save(GroupParticipant participant);

    Optional<GroupParticipant> findById(UUID participantId);

    Optional<GroupParticipant> findByGroupIdAndWaId(UUID expenseGroupId, WaId waId);

    List<GroupParticipant> findByGroupId(UUID expenseGroupId);

    List<GroupParticipant> findActiveByGroupId(UUID expenseGroupId);

    void updateActiveStatus(UUID participantId, boolean active);
}