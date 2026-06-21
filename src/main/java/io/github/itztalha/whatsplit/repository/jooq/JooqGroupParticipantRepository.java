package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.jooq.tables.records.GroupParticipantsRecord;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.GroupParticipant;
import io.github.itztalha.whatsplit.repository.GroupParticipantRepository;
import io.github.itztalha.whatsplit.repository.mapper.GroupParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.itztalha.whatsplit.jooq.Tables.GROUP_PARTICIPANTS;

@Repository
@RequiredArgsConstructor
public class JooqGroupParticipantRepository implements GroupParticipantRepository {

    private final DSLContext dsl;
    private final GroupParticipantMapper mapper;

    @Override
    public GroupParticipant save(
            GroupParticipant participant
    ) {
        GroupParticipantsRecord record = dsl.newRecord(GROUP_PARTICIPANTS);

        record.setExpenseGroupId(participant.expenseGroupId());
        record.setWaId(participant.waId().value());
        record.setIsActive(participant.active());

        record.store();

        return mapper.toModel(record);
    }

    @Override
    public Optional<GroupParticipant> findById(
            UUID participantId
    ) {
        return dsl.selectFrom(GROUP_PARTICIPANTS)
                .where(
                        GROUP_PARTICIPANTS.ID.eq(
                                participantId
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public Optional<GroupParticipant> findByGroupIdAndWaId(
            UUID expenseGroupId,
            WaId waId
    ) {
        return dsl.selectFrom(GROUP_PARTICIPANTS)
                .where(
                        GROUP_PARTICIPANTS.EXPENSE_GROUP_ID.eq(
                                expenseGroupId
                        )
                )
                .and(
                        GROUP_PARTICIPANTS.WA_ID.eq(
                                waId.value()
                        )
                )
                .fetchOptional(mapper::toModel);
    }

    @Override
    public List<GroupParticipant> findByGroupId(
            UUID expenseGroupId
    ) {
        return dsl.selectFrom(GROUP_PARTICIPANTS)
                .where(
                        GROUP_PARTICIPANTS.EXPENSE_GROUP_ID.eq(
                                expenseGroupId
                        )
                )
                .orderBy(
                        GROUP_PARTICIPANTS.IS_ACTIVE.desc(),
                        GROUP_PARTICIPANTS.WA_ID.asc(),
                        GROUP_PARTICIPANTS.ID.asc()
                )
                .fetch(mapper::toModel);
    }

    @Override
    public List<GroupParticipant> findActiveByGroupId(
            UUID expenseGroupId
    ) {
        return dsl.selectFrom(GROUP_PARTICIPANTS)
                .where(
                        GROUP_PARTICIPANTS.EXPENSE_GROUP_ID.eq(
                                expenseGroupId
                        )
                )
                .and(
                        GROUP_PARTICIPANTS.IS_ACTIVE.isTrue()
                )
                .orderBy(
                        GROUP_PARTICIPANTS.WA_ID,
                        GROUP_PARTICIPANTS.ID
                )
                .fetch(mapper::toModel);
    }

    @Override
    public void updateActiveStatus(
            UUID participantId,
            boolean active
    ) {
        dsl.update(GROUP_PARTICIPANTS)
                .set(
                        GROUP_PARTICIPANTS.IS_ACTIVE,
                        active
                )
                .where(
                        GROUP_PARTICIPANTS.ID.eq(
                                participantId
                        )
                )
                .execute();
    }
}