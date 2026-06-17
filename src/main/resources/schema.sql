CREATE TABLE IF NOT EXISTS binary_contents (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    status varchar(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS channels (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    profile_id UUID UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    CONSTRAINT fk_user_profile FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS user_statuses (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    user_id UUID NOT NULL UNIQUE,
    last_active_at timestamp with time zone NOT NULL,
    CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,
    CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS read_statuses (
    id UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at timestamp with time zone NOT NULL,
    notification_enabled boolean NOT NULL,
    CONSTRAINT fk_read_status_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    UNIQUE (user_id, channel_id)
    );

CREATE TABLE IF NOT EXISTS message_attachments (
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_attachment_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_content FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    receiver_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
    );
-- 알림 목록 조회 시 읽기 성능을 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_notifications_receiver_created_at ON notifications (receiver_id, created_at DESC);