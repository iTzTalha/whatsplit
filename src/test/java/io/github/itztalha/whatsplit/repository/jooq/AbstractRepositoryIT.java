package io.github.itztalha.whatsplit.repository.jooq;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.github.itztalha.whatsplit.jooq.Tables.*;

@SpringBootTest
@Testcontainers
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