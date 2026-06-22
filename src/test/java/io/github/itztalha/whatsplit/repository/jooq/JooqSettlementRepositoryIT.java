package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.model.common.CurrencyCode;
import io.github.itztalha.whatsplit.model.common.WaChatId;
import io.github.itztalha.whatsplit.model.common.WaId;
import io.github.itztalha.whatsplit.model.group.ExpenseGroup;
import io.github.itztalha.whatsplit.model.settlement.Settlement;
import io.github.itztalha.whatsplit.repository.ExpenseGroupRepository;
import io.github.itztalha.whatsplit.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JooqSettlementRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private SettlementRepository repository;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Test
    void shouldSaveSettlement() {
        ExpenseGroup group = createExpenseGroup("Goa Trip");

        Settlement settlement =
                new Settlement(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        new WaId("918888888888"),
                        new BigDecimal("500.00"),
                        new CurrencyCode("INR")
                );

        Settlement saved =
                repository.save(settlement);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.expenseGroupId())
                .isEqualTo(group.id());
        assertThat(saved.fromWaId())
                .isEqualTo(new WaId("919999999999"));
        assertThat(saved.toWaId())
                .isEqualTo(new WaId("918888888888"));
        assertThat(saved.amount())
                .isEqualByComparingTo("500.00");
        assertThat(saved.currencyCode())
                .isEqualTo(new CurrencyCode("INR"));
    }

    @Test
    void shouldFindSettlementById() {
        ExpenseGroup group = createExpenseGroup("Goa Trip");

        Settlement saved =
                repository.save(
                        new Settlement(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                new WaId("918888888888"),
                                new BigDecimal("500.00"),
                                new CurrencyCode("INR")
                        )
                );

        Optional<Settlement> found =
                repository.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get())
                .isEqualTo(saved);
    }

    @Test
    void shouldReturnEmptyWhenSettlementDoesNotExist() {
        assertThat(
                repository.findById(UUID.randomUUID())
        ).isEmpty();
    }

    @Test
    void shouldFindSettlementsByExpenseGroupId() {
        ExpenseGroup group =
                createExpenseGroup("Goa Trip");

        Settlement first =
                repository.save(
                        new Settlement(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                new WaId("918888888888"),
                                new BigDecimal("500.00"),
                                new CurrencyCode("INR")
                        )
                );

        Settlement second =
                repository.save(
                        new Settlement(
                                null,
                                group.id(),
                                new WaId("917777777777"),
                                new WaId("916666666666"),
                                new BigDecimal("300.00"),
                                new CurrencyCode("INR")
                        )
                );

        ExpenseGroup anotherGroup =
                createExpenseGroup("Kerala Trip");

        repository.save(
                new Settlement(
                        null,
                        anotherGroup.id(),
                        new WaId("915555555555"),
                        new WaId("914444444444"),
                        new BigDecimal("100.00"),
                        new CurrencyCode("INR")
                )
        );

        assertThat(
                repository.findByGroupId(
                        group.id()
                )
        )
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        first,
                        second
                );
    }

    @Test
    void shouldReturnEmptyListWhenExpenseGroupHasNoSettlements() {
        ExpenseGroup group =
                createExpenseGroup("Goa Trip");

        assertThat(
                repository.findByGroupId(
                        group.id()
                )
        ).isEmpty();
    }

    @Test
    void shouldRejectSettlementWithNonPositiveAmount() {
        ExpenseGroup group =
                createExpenseGroup("Goa Trip");

        assertThatThrownBy(() ->
                repository.save(
                        new Settlement(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                new WaId("918888888888"),
                                BigDecimal.ZERO,
                                new CurrencyCode("INR")
                        )
                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                )
                .hasMessageContaining(
                        "chk_settlements_amount_positive"
                );
    }

    @Test
    void shouldRejectSettlementBetweenSameParticipant() {
        ExpenseGroup group =
                createExpenseGroup("Goa Trip");

        assertThatThrownBy(() ->
                repository.save(
                        new Settlement(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                new WaId("919999999999"),
                                new BigDecimal("500.00"),
                                new CurrencyCode("INR")
                        )
                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                )
                .hasMessageContaining(
                        "chk_settlements_different_participants"
                );
    }

    @Test
    void shouldDeleteSettlementsWhenExpenseGroupIsDeleted() {
        ExpenseGroup group =
                createExpenseGroup("Goa Trip");

        Settlement settlement =
                repository.save(
                        new Settlement(
                                null,
                                group.id(),
                                new WaId("919999999999"),
                                new WaId("918888888888"),
                                new BigDecimal("500.00"),
                                new CurrencyCode("INR")
                        )
                );

        expenseGroupRepository.deleteById(
                group.id()
        );

        assertThat(
                repository.findById(
                        settlement.id()
                )
        ).isEmpty();
    }

    @Test
    void shouldAllowMultipleSettlementsBetweenSameParticipants() {
        ExpenseGroup group =
                createExpenseGroup("Goa Trip");

        repository.save(
                new Settlement(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        new WaId("918888888888"),
                        new BigDecimal("200.00"),
                        new CurrencyCode("INR")
                )
        );

        repository.save(
                new Settlement(
                        null,
                        group.id(),
                        new WaId("919999999999"),
                        new WaId("918888888888"),
                        new BigDecimal("300.00"),
                        new CurrencyCode("INR")
                )
        );

        assertThat(
                repository.findByGroupId(group.id())
        ).hasSize(2);
    }

    private ExpenseGroup createExpenseGroup(
            String name
    ) {
        return expenseGroupRepository.save(
                new ExpenseGroup(
                        null,
                        new WaChatId("120363123456789@g.us"),
                        name,
                        null,
                        new WaId("919999999999")
                )
        );
    }
}