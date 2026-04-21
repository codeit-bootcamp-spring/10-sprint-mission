-- 0. 기존 테이블 및 타입 초기화
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- 2.  바이너리 파일을 관리하는 테이블
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    file_name    VARCHAR(225) not null,
    size         BIGINT not null ,
    content_type VARCHAR(100) not null,
    created_at   TIMESTAMPTZ NOT NULL
);

-- 3. 채널을 관리하는 테이블
CREATE TABLE channels
(
    id           UUID PRIMARY KEY,
    channel_name VARCHAR(100),
    description  VARCHAR(500),
    channel_type VARCHAR(20) CHECK (channel_type IN ('PRIVATE', 'PUBLIC')) NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL,
    updated_at   TIMESTAMPTZ
);

-- 4. 유저를 관리하는 테이블
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username  VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID UNIQUE REFERENCES binary_contents (id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ         NOT NULL,
    updated_at TIMESTAMPTZ
);

-- 5. 유저 온라인 상태 확인 테이블
CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    user_id        UUID UNIQUE NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    last_active_at TIMESTAMPTZ NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ
);

-- 6. 메시지 관리 테이블
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    content    TEXT,
    author_id  UUID      REFERENCES users (id) ON DELETE SET NULL,
    channel_id UUID      NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

-- ⭐️ 7. 메시지와 바이너리 컨텐츠를 연관관계를 관리하는 조인 테이블
-- message, user, binary-content의 참조 순환을 해결!
CREATE TABLE message_attachments
(
    message_id        UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE
);

-- 8. 읽음 시간 관리 테이블
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    last_read_at TIMESTAMPTZ NOT NULL,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    CONSTRAINT unique_user_channel UNIQUE (user_id, channel_id)
);