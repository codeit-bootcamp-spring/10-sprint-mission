CREATE TABLE binary_contents (
    id UUID PRIMARY KEY,
    message_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    status varchar(100) NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    profile_id UUID UNIQUE,
    role varchar(20) NOT NULL,

    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

CREATE TABLE channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK ( type IN ('PUBLIC', 'PRIVATE') )
);

CREATE TABLE read_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    notification_enabled boolean NOT NULL,

    CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_statuses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,

    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_user FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE message_attachments (
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,

    CONSTRAINT fk_message_attachments_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_attachments_binary_contents FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);