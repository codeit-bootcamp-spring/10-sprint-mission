-- PostgreSQL UUID 생성 함수 사용을 위한 확장
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- binary_contents

CREATE TABLE IF NOT EXISTS binary_contents
(
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at   timestamptz  NOT NULL DEFAULT now(),
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL,
    bytes        bytea        NOT NULL
);

-- users
create table if not exists users
(
    id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz  NOT NULL DEFAULT now(),
    username   varchar(50)  NOT NULL UNIQUE,
    email      varchar(100) NOT NULL UNIQUE,
    password   varchar(60)  NOT NULL,
    profile_id uuid         NULL UNIQUE,

    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents
            ON DELETE SET NULL
);
-- userstatuses
CREATE TABLE IF NOT EXISTS user_statuses
(
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at     timestamptz NOT NULL DEFAULT now(),
    updated_at     timestamptz NOT NULL DEFAULT now(),
    user_id        uuid        NOT NULL UNIQUE ,
    last_active_at timestamptz NOT NULL,

    CONSTRAINT fk_user_statuses_user
        FOREIGN KEY (user_id)
            REFERENCES users (id) ON DELETE CASCADE
);
-- channels
CREATE TABLE IF NOT EXISTS channels
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now(),
    name        varchar(100),
    description varchar(500),
    type        varchar(10) NOT NULL,
    CONSTRAINT ck_channels_type CHECK (type IN ('PUBLIC', 'PRIVATE'))
);
-- read_statuses

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at   timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now(),
    user_id      uuid        NOT NULL,
    channel_id   uuid        NOT NULL,
    last_read_at timestamptz NOT NULL,
    UNIQUE (user_id, channel_id),

    CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_read_statuses_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE
);

--  messages

CREATE TABLE IF NOT EXISTS messages
(
    id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    content    text,
    channel_id uuid NOT NULL,
    author_id  uuid NULL,

    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL
);
-- message_attachments
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

