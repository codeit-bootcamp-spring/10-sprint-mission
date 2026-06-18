CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    bytes        BYTEA                    NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    status       varchar(20)              NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   timestamp with time zone
);

/*
 파일고도화에 따라 바이트 열 삭제 필요
 */
ALTER TABLE binary_contents
    DROP COLUMN bytes;

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(50)              NOT NULL UNIQUE,
    email      VARCHAR(100)             NOT NULL UNIQUE,
    password   VARCHAR(60)              NOT NULL,
    role       VARCHAR(20)              NOT NULL,
    profile_id UUID UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_profile_id
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL
);



-- CREATE TABLE user_statuses
-- (
--     id             UUID PRIMARY KEY,
--     user_id        UUID UNIQUE              NOT NULL REFERENCES users (id) ON DELETE CASCADE,
--     last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,
--     created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
--     updated_at     TIMESTAMP WITH TIME ZONE
-- );

CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10)              NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE')),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE
);

CREATE TABLE read_statuses
(
    id                   UUID PRIMARY KEY,
    user_id              UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id           UUID                     NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE,
    notification_enabled boolean                  NOT NULL,
    CONSTRAINT user_channel_id UNIQUE (user_id, channel_id)
);

CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    content    TEXT,
    author_id  UUID                     REFERENCES users (id) ON DELETE SET NULL,
    channel_id UUID                     NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE
);

CREATE TABLE persistent_logins
(
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);

CREATE TABLE notifications
(
    id          UUID PRIMARY KEY,
    title       VARCHAR(100)             NOT NULL,
    content     VARCHAR(255)             NOT NULL,
    receiver_id UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
)