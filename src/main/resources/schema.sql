CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255)             NOT NULL UNIQUE,
    password   VARCHAR(255)             NOT NULL,
    nickname   VARCHAR(20)              NOT NULL,
    status     VARCHAR(30)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE books
(
    id             UUID PRIMARY KEY,
    title          VARCHAR(255)             NOT NULL,
    author         VARCHAR(255)             NOT NULL,
    description    TEXT                     NOT NULL,
    publisher      VARCHAR(255)             NOT NULL,
    published_date DATE                     NOT NULL,
    isbn           VARCHAR(20)              NOT NULL UNIQUE,
    thumbnail_url  VARCHAR(500),
    review_count   INTEGER                  NOT NULL DEFAULT 0 CHECK (review_count >= 0),
    rating         DOUBLE PRECISION         NOT NULL DEFAULT 0 CHECK (rating BETWEEN 0 AND 5),
    book_status    VARCHAR(30)              NOT NULL,
    deleted_at     TIMESTAMP WITH TIME ZONE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE
);

CREATE TABLE reviews
(
    id            UUID PRIMARY KEY,
    book_id       UUID                     NOT NULL,
    user_id       UUID                     NOT NULL,
    rating        INTEGER                  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content       TEXT                     NOT NULL,
    like_count    INTEGER                  NOT NULL DEFAULT 0 CHECK (like_count >= 0),
    comment_count INTEGER                  NOT NULL DEFAULT 0 CHECK (comment_count >= 0),
    status        VARCHAR(30)              NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE,
    deleted_at    TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_book_user_active
    ON reviews (book_id, user_id, deleted_at);

CREATE TABLE comments
(
    id         UUID PRIMARY KEY,
    review_id  UUID                     NOT NULL,
    user_id    UUID                     NOT NULL,
    content    VARCHAR(500)             NOT NULL,
    status     VARCHAR(30)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE notifications
(
    id             UUID PRIMARY KEY,
    user_id        UUID                     NOT NULL,
    review_id      UUID                     NOT NULL,
    review_content TEXT                     NOT NULL,
    message        VARCHAR(255)             NOT NULL,
    confirmed      BOOLEAN                  NOT NULL DEFAULT false,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_confirmed_updated_at ON notifications (confirmed, updated_at);

CREATE TABLE review_likes
(
    id         UUID PRIMARY KEY,
    review_id  UUID                     NOT NULL,
    user_id    UUID                     NOT NULL,
    liked_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
ALTER TABLE review_likes
    ADD CONSTRAINT uk_review_user_like UNIQUE (review_id, user_id);
CREATE INDEX idx_review_likes_liked_at_review_id ON review_likes (liked_at, review_id);
CREATE INDEX idx_review_likes_liked_at_user_id ON review_likes (liked_at, user_id);


CREATE TABLE power_users
(
    id               UUID PRIMARY KEY,
    snapshot_id      UUID                     NOT NULL,
    user_id          UUID                     NOT NULL,
    period_type      VARCHAR(30)              NOT NULL,
    period_start     TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end       TIMESTAMP WITH TIME ZONE NOT NULL,
    rank             BIGINT                   NOT NULL,
    score            DOUBLE PRECISION         NOT NULL,
    review_score_sum DOUBLE PRECISION         NOT NULL DEFAULT 0,
    like_count       BIGINT                   NOT NULL DEFAULT 0 CHECK (like_count >= 0),
    comment_count    BIGINT                   NOT NULL DEFAULT 0 CHECK (comment_count >= 0),
    aggregated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

ALTER TABLE power_users
    ADD CONSTRAINT uk_power_users_period UNIQUE (user_id, period_type, period_start, period_end);

CREATE INDEX idx_power_users_period_start_rank ON power_users (period_type, period_start, rank);
CREATE INDEX idx_power_users_snapshot_rank ON power_users (snapshot_id, rank);


CREATE TABLE popular_reviews
(
    id            UUID PRIMARY KEY,
    review_id     UUID                     NOT NULL,
    period_type   VARCHAR(30)              NOT NULL,
    period_start  TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end    TIMESTAMP WITH TIME ZONE NOT NULL,
    rank          BIGINT                   NOT NULL,
    score         DOUBLE PRECISION         NOT NULL,
    like_count    BIGINT                   NOT NULL DEFAULT 0 CHECK (like_count >= 0),
    comment_count BIGINT                   NOT NULL DEFAULT 0 CHECK (comment_count >= 0),
    snapshot_id   UUID                     NOT NULL,
    aggregated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE
);
ALTER TABLE popular_reviews
    ADD CONSTRAINT uk_popular_reviews_period UNIQUE (review_id, period_type, period_start, snapshot_id);

CREATE INDEX idx_popular_reviews_period_start_rank ON popular_reviews (period_type, period_start, rank);
CREATE INDEX idx_popular_reviews_snapshot_rank ON popular_reviews (snapshot_id, rank);

CREATE TABLE popular_books
(
    id           UUID PRIMARY KEY,
    book_id      UUID                     NOT NULL,
    period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end   TIMESTAMP WITH TIME ZONE NOT NULL,
    review_count BIGINT                   NOT NULL DEFAULT 0 CHECK (review_count >= 0),
    avg_rating   DOUBLE PRECISION         NOT NULL,
    score        DOUBLE PRECISION         NOT NULL,
    rank         BIGINT                   NOT NULL,
    period_type  VARCHAR(30)              NOT NULL,
    snapshot_id  UUID                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
);

CREATE INDEX idx_book_id_period_type_snap_shot_id ON popular_books (book_id, period_type, snapshot_id);
CREATE INDEX idx_period_snapshot_window_score ON popular_books (period_type, snapshot_id,
                                                                    period_start, period_end,
                                                                    score);


CREATE TABLE aggregate_snapshot
(
    id            UUID PRIMARY KEY,
    snapshot_id   UUID                     NOT NULL UNIQUE,
    period_type   VARCHAR(30)              NOT NULL,
    domain_type   VARCHAR(30)              NOT NULL,
    aggregated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    staging_type  VARCHAR(30)              NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_aggregate_domain_snapshots_period_status ON aggregate_snapshot (domain_type, period_type, staging_type);
