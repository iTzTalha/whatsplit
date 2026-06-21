package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import io.github.itztalha.whatsplit.model.group.GroupParticipant;
import io.github.itztalha.whatsplit.repository.ExpenseGroupRepository;
import io.github.itztalha.whatsplit.repository.GroupParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class JooqGroupParticipantRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Autowired
    private GroupParticipantRepository participantRepository;

    @Test
    void shouldSaveParticipant() {
        ExpenseGroup group = createGroup();

        GroupParticipant participant =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        assertThat(participant.id()).isNotNull();
        assertThat(participant.expenseGroupId())
                .isEqualTo(group.id());
        assertThat(participant.waId())
                .isEqualTo(new WaId("919999999999"));
        assertThat(participant.active())
                .isTrue();
    }

    @Test
    void shouldFindParticipantById() {
        ExpenseGroup group = createGroup();

        GroupParticipant saved =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        Optional<GroupParticipant> found =
                participantRepository.findById(
                        saved.id()
                );

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    void shouldFindParticipantByGroupIdAndWaId() {
        ExpenseGroup group = createGroup();

        GroupParticipant saved =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        Optional<GroupParticipant> found =
                participantRepository.findByGroupIdAndWaId(
                        group.id(),
                        new WaId("919999999999")
                );

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    void shouldFindParticipantsByGroupId() {
        ExpenseGroup group = createGroup();

        GroupParticipant first =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        GroupParticipant second =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("918888888888"),
                                false
                        )
                );

        List<GroupParticipant> participants =
                participantRepository.findByGroupId(
                        group.id()
                );

        assertThat(participants)
                .containsExactlyInAnyOrder(
                        first,
                        second
                );
    }

    @Test
    void shouldFindOnlyActiveParticipants() {
        ExpenseGroup group = createGroup();

        GroupParticipant active =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        participantRepository.save(
                new GroupParticipant(
                        null,
                        group.id(),
                        new WaId("918888888888"),
                        false
                )
        );

        List<GroupParticipant> participants =
                participantRepository.findActiveByGroupId(
                        group.id()
                );

        assertThat(participants)
                .containsExactly(active);
    }

    @Test
    void shouldDeactivateParticipant() {
        ExpenseGroup group = createGroup();

        GroupParticipant participant =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        participantRepository.updateActiveStatus(
                participant.id(),
                false
        );

        GroupParticipant updated =
                participantRepository.findById(
                                participant.id()
                        )
                        .orElseThrow();

        assertThat(updated.active()).isFalse();
    }

    @Test
    void shouldActivateParticipant() {
        ExpenseGroup group = createGroup();

        GroupParticipant participant =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                false
                        )
                );

        participantRepository.updateActiveStatus(
                participant.id(),
                true
        );

        GroupParticipant updated =
                participantRepository.findById(
                                participant.id()
                        )
                        .orElseThrow();

        assertThat(updated.active()).isTrue();
    }

    @Test
    void shouldEnforceUniqueParticipantPerGroup() {
        ExpenseGroup group = createGroup();

        participantRepository.save(
                new GroupParticipant(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        true
                )
        );

        assertThatThrownBy(() ->
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                )
        ).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    private ExpenseGroup createGroup() {
        return expenseGroupRepository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363123456789@g.us"),
                        "Goa Trip",
                        null,
                        new WaId("919999999999")
                )
        );
    }

    @Test
    void shouldReturnEmptyWhenParticipantDoesNotExist() {
        assertThat(
                participantRepository.findById(
                        UUID.randomUUID()
                )
        ).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenParticipantDoesNotExistForGroupAndWaId() {
        ExpenseGroup group = createGroup();

        assertThat(
                participantRepository.findByGroupIdAndWaId(
                        group.id(),
                        new WaId("919999999999")
                )
        ).isEmpty();
    }

    @Test
    void shouldDeleteParticipantsWhenGroupIsDeleted() {
        ExpenseGroup group = createGroup();

        GroupParticipant participant =
                participantRepository.save(
                        new GroupParticipant(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                true
                        )
                );

        expenseGroupRepository.deleteById(
                group.id()
        );

        assertThat(
                participantRepository.findById(
                        participant.id()
                )
        ).isEmpty();
    }
}