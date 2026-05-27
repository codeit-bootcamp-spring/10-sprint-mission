CREATE TABLE binary_contents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

CREATE TABLE channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NUll CHECK (type in ('PUBLIC', 'PRIVATE'))
);


CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    profile_id UUID UNIQUE,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id)
        REFERENCES binary_contents(id)
        ON DELETE SET NULL
);

CREATE TABLE read_statuses (
    id UUID PRIMARY KEY ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    user_id UUID NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
       REFERENCES users(id)
       ON DELETE CASCADE,
    channel_id UUID NOT NULL,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id)
       REFERENCES channels(id)
       ON DELETE CASCADE,
    last_read_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    channel_id UUID NOT NULL,
    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE,
    author_id UUID NOT NULL,
    CONSTRAINT fk_messages_author FOREIGN KEY (author_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    content VARCHAR(500) NOT NULL
);

CREATE TABLE message_attachments (
    message_id UUID NOT NULL,
    CONSTRAINT fk_message_attachments_message FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,
    attachment_id UUID NOT NULL,
    CONSTRAINT fk_message_attachments_attachment FOREIGN KEY (attachment_id)
        REFERENCES binary_contents(id)
        ON DELETE CASCADE
);

ALTER TABLE read_statuses DROP CONSTRAINT fk_user_id;
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;