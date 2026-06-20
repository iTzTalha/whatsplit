CREATE TABLE expense_participants
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    expense_id UUID           NOT NULL,

    wa_id      TEXT           NOT NULL,

    amount     NUMERIC(12, 2) NOT NULL,

    CONSTRAINT fk_expense_participants_expense FOREIGN KEY (expense_id) REFERENCES expenses (id) ON DELETE CASCADE,

    CONSTRAINT uq_expense_participant UNIQUE (expense_id, wa_id),

    CONSTRAINT chk_expense_participants_amount_positive CHECK (amount > 0)
);