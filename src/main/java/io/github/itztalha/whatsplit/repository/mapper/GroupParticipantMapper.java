package io.github.itztalha.whatsplit.repository.mapper;

import io.github.itztalha.whatsplit.jooq.tables.records.GroupParticipantsRecord;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.GroupParticipant;
import org.springframework.stereotype.Component;

@Component
public class GroupParticipantMapper {

    public GroupParticipant toModel(GroupParticipantsRecord record) {
        return new GroupParticipant(
                record.getId(),
                record.getExpenseGroupId(),
                new WaId(record.getWaId()),
                record.getIsActive()
        );
    }
}