DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at timestamptz         NOT NULL,
    updated_at timestamptz,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID,
    role       varchar(20)         NOT NULL DEFAULT USER
);

DROP TABLE IF EXISTS channels CASCADE;
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  timestamptz  NOT NULL,
    updated_at  timestamptz,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    type        VARCHAR(10)  NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

DROP TABLE IF EXISTS messages CASCADE;
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    TEXT        NOT NULL,
    channel_id UUID        NOT NULL,
    author_id  UUID        NOT NULL,
    CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_message_user
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

DROP TABLE IF EXISTS binary_contents CASCADE;
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BYTEA        NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL;

DROP TABLE IF EXISTS user_statuses CASCADE;
CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        UUID        NOT NULL UNIQUE,
    last_active_at timestamptz NOT NULL,
    online         BOOLEAN     NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_user_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

DROP TABLE IF EXISTS read_statuses CASCADE;
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    user_id      UUID        NOT NULL,
    channel_id   UUID        NOT NULL,
    last_read_at timestamptz NOT NULL,
    CONSTRAINT uq_read_status_user_channel UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE
);
