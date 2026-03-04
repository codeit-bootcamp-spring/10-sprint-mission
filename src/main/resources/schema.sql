DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS read_status CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

CREATE TABLE channels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC','PRIVATE'))
);

CREATE TABLE binary_contents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes BYTEA NOT NULL
);

CREATE TABLE  users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    profile_id UUID UNIQUE,
    FOREIGN KEY(profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

CREATE TABLE user_statuses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    user_id UUID UNIQUE NOT NULL,
    last_active_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE read_statuses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at TIMESTAMPTZ NOT NULL,
    UNIQUE(user_id,channel_id),
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(channel_id) REFERENCES channels(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,
    FOREIGN KEY(channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE message_attachments (
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,
    FOREIGN KEY(message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY(attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);