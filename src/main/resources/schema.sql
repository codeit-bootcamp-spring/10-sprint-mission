CREATE EXTENSION IF NOT EXISTS pgcrypto;

DROP TYPE IF EXISTS channel_type CASCADE;
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

-- binary_contents
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

-- users
CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID,

    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_profile_id UNIQUE (profile_id),
    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL
);

-- user_statuses
CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ,
    user_id        UUID        NOT NULL,
    last_active_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_user_statuses_user UNIQUE (user_id),
    CONSTRAINT fk_user_statuses_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

-- channels
CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        channel_type NOT NULL
);

-- messages
CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    content    TEXT,
    channel_id UUID        NOT NULL,
    author_id  UUID,

    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL
);

-- read_statuses
CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    user_id      UUID        NOT NULL,
    channel_id   UUID        NOT NULL,
    last_read_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_read_user_channel UNIQUE (user_id, channel_id),

    CONSTRAINT fk_read_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_read_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE
);

-- message_attachments (N:M)
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,

    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id),

    CONSTRAINT fk_ma_message
        FOREIGN KEY (message_id)
            REFERENCES messages (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ma_attachment
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_messages_channel ON messages (channel_id);
CREATE INDEX IF NOT EXISTS idx_read_user ON read_statuses (user_id);
CREATE INDEX IF NOT EXISTS idx_read_channel ON read_statuses (channel_id);