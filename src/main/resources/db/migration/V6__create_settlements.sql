CREATE TABLE settlements
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    expense_group_id UUID           NOT NULL,

    from_wa_id       TEXT           NOT NULL,

    to_wa_id         TEXT           NOT NULL,

    amount           NUMERIC(12, 2) NOT NULL,

    currency_code    CHAR(3)        NOT NULL,

    CONSTRAINT fk_settlements_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id) ON DELETE CASCADE,

    CONSTRAINT chk_settlements_amount_positive CHECK (amount > 0),

    CONSTRAINT chk_settlements_different_participants CHECK (from_wa_id <> to_wa_id)
);