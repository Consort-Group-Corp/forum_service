CREATE TABLE IF NOT EXISTS forum_schema.forum_complaint (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forum_id UUID NOT NULL REFERENCES forum_schema.forum(id) ON DELETE CASCADE,
    topic_id UUID REFERENCES forum_schema.forum_topic(id) ON DELETE CASCADE,
    comment_id UUID REFERENCES forum_schema.forum_comment(id) ON DELETE CASCADE,
    reporter_id UUID NOT NULL,
    offender_id UUID NOT NULL,
    reason VARCHAR(120) NOT NULL,
    status VARCHAR(32) NOT NULL,
    message_snapshot TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at TIMESTAMPTZ,
    resolved_by UUID
);
