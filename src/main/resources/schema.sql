DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

CREATE TABLE binary_contents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(60) NOT NULL,
    profile_id UUID,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_profile_id UNIQUE (profile_id),
    CONSTRAINT fk_users_profile_id
        FOREIGN KEY (profile_id)
        REFERENCES binary_contents (id)
        ON DELETE SET NULL
);

CREATE TABLE channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    last_message_at TIMESTAMPTZ,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL,
    CONSTRAINT chk_channels_type CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE user_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    user_id UUID NOT NULL,
    last_active_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_user_statuses_user_id UNIQUE (user_id),
    CONSTRAINT fk_user_statuses_user_id
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE read_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_statuses_user_id
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel_id
        FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,
    CONSTRAINT fk_messages_channel_id
        FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_messages_author_id
        FOREIGN KEY (author_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);

CREATE TABLE message_attachments (
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,
    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_attachments_message_id
        FOREIGN KEY (message_id)
        REFERENCES messages (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_message_attachments_attachment_id
        FOREIGN KEY (attachment_id)
        REFERENCES binary_contents (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_read_statuses_channel_id ON read_statuses (channel_id);
CREATE INDEX idx_messages_channel_id ON messages (channel_id);
CREATE INDEX idx_messages_author_id ON messages (author_id);
CREATE INDEX idx_message_attachments_attachment_id ON message_attachments (attachment_id);
CREATE INDEX idx_messages_channel_created_id ON messages (channel_id, created_at DESC, id DESC);
