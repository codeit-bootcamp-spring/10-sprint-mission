CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    status       varchar(20)              NOT NULL CHECK (status IN ('PROCESSING', 'SUCCESS', 'FAIL'))
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username   VARCHAR(50) UNIQUE       NOT NULL,
    email      VARCHAR(100) UNIQUE      NOT NULL,
    password   VARCHAR(60)              NOT NULL,
    profile_id UUID UNIQUE,
    role       VARCHAR(20)              NOT NULL CHECK (role IN ('ADMIN', 'CHANNEL_MANAGER', 'USER')),
    FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10)              NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content    TEXT                     NOT NULL,
    author_id  UUID,
    channel_id UUID                     NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id                   UUID PRIMARY KEY,
    created_at           timestamp with time zone NOT NULL,
    updated_at           timestamp with time zone,
    user_id              UUID                     NOT NULL,
    channel_id           UUID                     NOT NULL,
    last_read_at         timestamp with time zone NOT NULL,
    notification_enabled boolean                  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS notifications
(
    id          UUID PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    receiver_id UUID                     NOT NULL,
    title       VARCHAR(100)             NOT NULL,
    content     VARCHAR(500)             NOT NULL
);