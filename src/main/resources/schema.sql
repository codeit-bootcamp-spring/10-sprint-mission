CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ         NOT NULL,
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID UNIQUE,

    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ,
    user_id        UUID UNIQUE NOT NULL,
    last_active_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_user_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
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

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    user_id      UUID        NOT NULL,
    channel_id   UUID        NOT NULL,
    last_read_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_read_status UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,

    PRIMARY KEY (message_id, attachment_id),

    CONSTRAINT fk_attachment_message
        FOREIGN KEY (message_id)
            REFERENCES messages (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_attachment_binary
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE
);