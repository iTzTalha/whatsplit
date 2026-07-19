package io.github.itztalha.whatsplit.repository.jooq;

import io.github.itztalha.whatsplit.TestcontainersConfiguration;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.github.itztalha.whatsplit.jooq.Tables.*;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
abstract class AbstractRepositoryIT {

    @Autowired
    protected DSLContext dsl;

    @BeforeEach
    void cleanup() {
        dsl.truncate(
                SETTLEMENTS,
                EXPENSE_PARTICIPANTS,
                EXPENSES,
                GROUP_PARTICIPANTS,
                EXPENSE_GROUPS
                )
                .cascade()
                .execute();
    }
}
