-- 채널 타입
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

-- 바이너리 컨텐츠 테이블
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    status       VARCHAR(20)              NOT NULL
);

-- 유저 테이블
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50)              NOT NULL UNIQUE,
    email      VARCHAR(100)             NOT NULL UNIQUE,
    password   VARCHAR(60)              NOT NULL,
    role       VARCHAR(20)              NOT NULL,
    profile_id UUID,

    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL
);

-- 채널 테이블
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        channel_type             NOT NULL
);

-- 읽음 상태 테이블
CREATE TABLE read_statuses
(
    id                   UUID PRIMARY KEY,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE,
    user_id              UUID                     NOT NULL,
    channel_id           UUID                     NOT NULL,
    last_active_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    notification_enabled BOOLEAN                  NOT NULL,

    CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_user_channel UNIQUE (user_id, channel_id)
);

-- 메시지 테이블
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    TEXT,
    channel_id UUID                     NOT NULL,
    author_id  UUID,

    CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_message_author
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL
);

-- 메시지 첨부파일 테이블
CREATE TABLE message_attachments
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

-- 알림 테이블
CREATE TABLE notifications
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    receiver_id UUID                     NOT NULL,
    title       VARCHAR(255)             NOT NULL,
    content     TEXT                     NOT NULL
);
