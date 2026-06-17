CREATE TABLE group_participants
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    expense_group_id UUID    NOT NULL,

    wa_id            TEXT    NOT NULL,

    is_active        BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_group_participants_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id) ON DELETE CASCADE,

    CONSTRAINT uq_group_participant UNIQUE (expense_group_id, wa_id)
);