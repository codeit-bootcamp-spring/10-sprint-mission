-- binary_contents : users = 0..1 : 1
-- binary_contents : message_attachments = 1 : 1
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    updated_at   timestamptz  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    status       VARCHAR(20)  NOT NULL
);

-- binary_contents : users = 0..1 : 1
-- read_statuses : users = 0..N : 1
-- messages : users = 0..N : 1
CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at timestamptz         NOT NULL,
    updated_at timestamptz,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID UNIQUE,
    role       VARCHAR(20)         NOT NULL,
    FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- users : notifications = M: 0...N
CREATE TABLE IF NOT EXISTS notifications
(
    id          UUID PRIMARY KEY,
    created_at  timestamptz  NOT NULL,
    updated_at  timestamptz,
    receiver_id UUID         NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     text,
    FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
);

-- read_status : channels = 0..N : 1
-- messages : channels = 0..N : 1
CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('PRIVATE', 'PUBLIC'))
);

-- channels : users = 0..M : 0..N
CREATE TABLE IF NOT EXISTS read_statuses
(
    id                   UUID PRIMARY KEY,
    created_at           timestamptz NOT NULL,
    updated_at           timestamptz,
    user_id              UUID        NOT NULL,
    channel_id           UUID        NOT NULL,
    last_read_at         timestamptz NOT NULL,
    notification_enabled BOOLEAN     NOT NULL,
    UNIQUE (user_id, channel_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);


-- messages : users = 0..N : 1
-- messages : channels = 0..N : 1
-- messages : message_attachments = N : 1
CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    text,
    channel_id UUID        NOT NULL,
    author_id  UUID,
    FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- binary_contents : messages = M : N
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);
