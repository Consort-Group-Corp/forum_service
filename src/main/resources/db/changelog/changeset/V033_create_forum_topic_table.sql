CREATE TABLE IF NOT EXISTS forum_schema.forum_topic(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forum_id UUID NOT NULL REFERENCES forum_schema.forum(id) ON DELETE CASCADE,
    author_id UUID NOT NULL,
    title VARCHAR(120) NOT NULL,
    content TEXT NOT NULL,
    language_code VARCHAR(8) NOT NULL,
    lesson_ref_type VARCHAR(32),
    lesson_ref_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
)