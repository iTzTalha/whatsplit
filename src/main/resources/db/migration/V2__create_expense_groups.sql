CREATE TABLE expense_groups
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    whatsapp_chat_id TEXT NOT NULL,

    name             TEXT NOT NULL,
    description      TEXT,

    created_by_wa_id TEXT NOT NULL,

    CONSTRAINT uq_expense_group_name UNIQUE (whatsapp_chat_id, name)
);