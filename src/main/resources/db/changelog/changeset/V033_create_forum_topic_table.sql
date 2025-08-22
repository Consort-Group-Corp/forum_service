CREATE TABLE IF NOT EXISTS forum_schema.forum_topic(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forum_id UUID NOT NULL REFERENCES forum_schema.forum(id) ON DELETE CASCADE,
    author_id UUID NOT NULL,
    title VARCHAR(120) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
)