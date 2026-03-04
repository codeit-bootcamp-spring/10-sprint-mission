CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    varchar(50) username UNIQUE NOT NULL,
    varchar(100) email UNIQUE NOT NULL,
    varchar(60) password NOT NULL,
    profile_id UUID,
    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
        REFERENCES binary_contents(id)
        ON DELETE SET NULL
);

CREATE TABLE user_statuses(
    id         UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id UUID unique,
    last_active_at timestamptz NOT NULL,
    CONSTRAINT fk_user_status_user
                          FOREIGN KEY(user_id)
                          REFERENCES users(id)
                          ON DELETE CASCADE
);

CREATE TABLE channels(
    id         UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK(type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE messages(
    id        UUID PRIMARY KEY ,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID NOT NULL,
    CONSTRAINT fk_message_channel
                     FOREIGN KEY (channel_id)
                     REFERENCES channels(id)
                     ON DELETE CASCADE,
    CONSTRAINT fk_message_user
                     FOREIGN KEY (author_id)
                     REFERENCES users(id)
                     ON DELETE SET NULL
);

CREATE TABLE read_statuses(
    id        UUID PRIMARY KEY ,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at timestamptz NOT NULL,
    CONSTRAINT read_status_user
                          FOREIGN KEY (user_id)
                          REFERENCES users(id)
                          ON DELETE CASCADE,
    CONSTRAINT read_status_channel
                          FOREIGN KEY (channel_id)
                          REFERENCES channels(id)
                          ON DELETE CASCADE
)

CREATE TABLE binary_contents(
    id        UUID PRIMARY KEY ,
    created_at timestamptz NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size LONG NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes BYTEA NOT NULL
)

CREATE TABLE message_attachments(
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,

    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id),

    CONSTRAINT fk_message_attachments_message
                                FOREIGN KEY (message_id)
                                REFERENCES message(id)
                                ON DELETE CASCADE,

    CONSTRAINT fk__message_attachments_attachment
                                FOREIGN KEY (attachment_id)
                                REFERENCES binary_contents(id)
                                ON DELETE CASCADE
);