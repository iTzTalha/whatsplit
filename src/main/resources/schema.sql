-- Generator-only PostgreSQL schema representation. Flyway migrations remain
-- the runtime schema source of truth. VARCHAR is used here because jOOQ's
-- H2-backed DDL interpreter cannot create a unique constraint on TEXT/CLOB.
CREATE TABLE expense_groups (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    whatsapp_chat_id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    description VARCHAR,
    created_by_wa_id VARCHAR NOT NULL,
    CONSTRAINT uq_expense_group_name UNIQUE (whatsapp_chat_id, name)
);

CREATE TABLE group_participants (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    expense_group_id UUID NOT NULL,
    wa_id VARCHAR NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_group_participants_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id) ON DELETE CASCADE,
    CONSTRAINT uq_group_participant UNIQUE (expense_group_id, wa_id)
);

CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    expense_group_id UUID NOT NULL,
    paid_by_wa_id VARCHAR NOT NULL,
    description VARCHAR,
    amount NUMERIC(12, 2) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    split_type VARCHAR NOT NULL,
    CONSTRAINT fk_expenses_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id) ON DELETE CASCADE,
    CONSTRAINT chk_expenses_amount_positive CHECK (amount > 0)
);

CREATE TABLE expense_participants (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    expense_id UUID NOT NULL,
    wa_id VARCHAR NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_expense_participants_expense FOREIGN KEY (expense_id) REFERENCES expenses (id) ON DELETE CASCADE,
    CONSTRAINT uq_expense_participant UNIQUE (expense_id, wa_id),
    CONSTRAINT chk_expense_participants_amount_positive CHECK (amount > 0)
);

CREATE TABLE settlements (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    expense_group_id UUID NOT NULL,
    from_wa_id VARCHAR NOT NULL,
    to_wa_id VARCHAR NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    CONSTRAINT fk_settlements_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id) ON DELETE CASCADE,
    CONSTRAINT chk_settlements_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_settlements_different_participants CHECK (from_wa_id <> to_wa_id)
);
