CREATE TABLE expenses
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    expense_group_id UUID           NOT NULL,

    paid_by_wa_id    TEXT           NOT NULL,

    description      TEXT,

    amount           NUMERIC(12, 2) NOT NULL,

    currency_code    CHAR(3)        NOT NULL,

    split_type       TEXT           NOT NULL,

    CONSTRAINT fk_expenses_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id) ON DELETE CASCADE,

    CONSTRAINT chk_expenses_amount_positive CHECK (amount > 0)
);