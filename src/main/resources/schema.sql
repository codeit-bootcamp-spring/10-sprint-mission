CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL DEFAULT now(),
    file_name    varchar(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type varchar(100)             NOT NULL
);

CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL DEFAULT now(),
    updated_at  timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)              NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_at timestamp with time zone,
    username   varchar(50) UNIQUE       NOT NULL,
    email      varchar(100) UNIQUE      NOT NULL,
    password   varchar(60)              NOT NULL,
    profile_id uuid UNIQUE,
    FOREIGN KEY (profile_id)
        REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamp with time zone NOT NULL DEFAULT now(),
    updated_at     timestamp with time zone,
    user_id        uuid UNIQUE              NOT NULL,
    last_active_at timestamptz              NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE read_statuses
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL DEFAULT now(),
    updated_at   timestamp with time zone,
    user_id      uuid                     NOT NULL,
    channel_id   uuid                     NOT NULL,
    last_read_at timestamptz              NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id)
        REFERENCES channels (id) ON DELETE CASCADE
);

CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_at timestamp with time zone,
    content    TEXT,
    channel_id uuid                     NOT NULL,
    author_id  uuid,
    FOREIGN KEY (channel_id)
        REFERENCES channels (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id)
        REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE message_attachments
(
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    FOREIGN KEY (message_id)
        REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id)
        REFERENCES binary_contents (id) ON DELETE CASCADE
);
