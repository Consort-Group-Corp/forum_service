CREATE TABLE IF NOT EXISTS forum_schema.forum_forbidden_word (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    word VARCHAR(120) NOT NULL UNIQUE,
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    is_active  BOOLEAN NOT NULL DEFAULT true
);