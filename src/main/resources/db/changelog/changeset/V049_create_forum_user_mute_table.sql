CREATE TABLE IF NOT EXISTS forum_schema.forum_user_mute (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forum_id UUID NOT NULL,
    user_id UUID NOT NULL,
    reason VARCHAR(255) NOT NULL,
    issued_by UUID NOT NULL,
    mute_until TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);