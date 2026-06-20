package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import io.github.itztalha.whatsplit.repository.ExpenseGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JooqExpenseGroupRepositoryIT {

    @Autowired
    private ExpenseGroupRepository repository;

    @Test
    void shouldSaveExpenseGroup() {
        ExpenseGroup group = new ExpenseGroup(
                null,
                new WaChatId("120363123456789@g.us"),
                "Goa Trip",
                "Goa 2025",
                new WaId("919999999999")
        );

        ExpenseGroup saved = repository.save(group);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.chatId())
                .isEqualTo(new WaChatId("120363123456789@g.us"));
        assertThat(saved.name())
                .isEqualTo("Goa Trip");
        assertThat(saved.description())
                .isEqualTo("Goa 2025");
        assertThat(saved.createdByWaId())
                .isEqualTo(new WaId("919999999999"));
    }

    @Test
    void shouldFindExpenseGroupById() {
        ExpenseGroup saved = repository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363123456789@g.us"),
                        "Europe Trip",
                        null,
                        new WaId("919999999999")
                )
        );

        Optional<ExpenseGroup> found =
                repository.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    void shouldReturnTrueWhenGroupExists() {
        repository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363123456789@g.us"),
                        "Kerala Trip",
                        null,
                        new WaId("919999999999")
                )
        );

        boolean exists =
                repository.existsByChatIdAndName(
                        new WaChatId("120363123456789@g.us"),
                        "Kerala Trip"
                );

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenGroupDoesNotExist() {
        boolean exists =
                repository.existsByChatIdAndName(
                        new WaChatId("120363123456789@g.us"),
                        "Non Existing"
                );

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindGroupsByChatId() {
        WaChatId chatId = new WaChatId("120363123456789@g.us");

        ExpenseGroup first = repository.save(
                new ExpenseGroup(
                        null,
                        chatId,
                        "Goa Trip",
                        null,
                        new WaId("919999999999")
                )
        );

        ExpenseGroup second = repository.save(
                new ExpenseGroup(
                        null,
                        chatId,
                        "Kerala Trip",
                        null,
                        new WaId("918888888888")
                )
        );

        repository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363987654321@g.us"),
                        "Europe Trip",
                        null,
                        new WaId("917777777777")
                )
        );

        var groups = repository.findByChatId(chatId);

        assertThat(groups)
                .hasSize(2)
                .containsExactlyInAnyOrder(first, second);
    }

    @Test
    void shouldDeleteExpenseGroup() {
        ExpenseGroup saved = repository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363123456789@g.us"),
                        "Delete Me",
                        null,
                        new WaId("919999999999")
                )
        );

        repository.deleteById(saved.id());

        assertThat(
                repository.findById(saved.id())
        ).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenGroupNotFound() {
        assertThat(
                repository.findById(UUID.randomUUID())
        ).isEmpty();
    }


}