CREATE TABLE IF NOT EXISTS binary_contents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile_id UUID UNIQUE,
    role varchar(20) NOT NULL,

    CONSTRAINT fk_users_binary_contents
        FOREIGN KEY (profile_id)
        REFERENCES binary_contents(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id UUID NOT NULL UNIQUE,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_user_statuses_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL DEFAULT ('PUBLIC'),
    last_message_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT ck_channels_type
        CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS read_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_read_statuses_user_channel
        UNIQUE (user_id, channel_id),

    CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_read_statuses_channel
        FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,

    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_user
        FOREIGN KEY (author_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS message_attachments (
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,

    CONSTRAINT fk_message_attachments_message
        FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_attachments_attachment
        FOREIGN KEY (attachment_id)
        REFERENCES binary_contents(id)
        ON DELETE CASCADE
);
