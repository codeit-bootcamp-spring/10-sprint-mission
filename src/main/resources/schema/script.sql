create table binary_contents
(
    id           uuid                     not null
        primary key,
    created_at   timestamp with time zone not null,
    file_name    varchar(255)             not null,
    size         bigint                   not null,
    content_type varchar(100)             not null,
    bytes        bytea                    not null
);

alter table binary_contents
    owner to discodeit_user;

create table users
(
    id         uuid                     not null
        primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    username   varchar(50)              not null
        unique,
    email      varchar(100)             not null
        unique,
    password   varchar(60)              not null,
    profile_id uuid
        unique
                                        references binary_contents
                                            on delete set null
);

alter table users
    owner to discodeit_user;

create table channels
(
    id          uuid                     not null
        primary key,
    created_at  timestamp with time zone not null,
    updated_at  timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)              not null
        constraint channels_type_check
            check ((type)::text = ANY ((ARRAY ['PUBLIC'::character varying, 'PRIVATE'::character varying])::text[]))
);

alter table channels
    owner to discodeit_user;

create table messages
(
    id         uuid                     not null
        primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    content    text,
    channel_id uuid                     not null
        references channels
            on delete cascade,
    author_id  uuid
                                        references users
                                            on delete set null
);

alter table messages
    owner to discodeit_user;

create table message_attachments
(
    message_id    uuid not null
        references messages
            on delete cascade,
    attachment_id uuid not null
        references binary_contents
            on delete cascade
);

alter table message_attachments
    owner to discodeit_user;

create table user_statuses
(
    id             uuid                     not null
        primary key,
    created_at     timestamp with time zone not null,
    updated_at     timestamp with time zone,
    user_id        uuid
        unique
        references users
            on delete cascade,
    last_active_at timestamp with time zone not null
);

alter table user_statuses
    owner to discodeit_user;

create table readstatus
(
    id           uuid                     not null
        primary key,
    created_at   timestamp with time zone not null,
    updated_at   timestamp with time zone,
    user_id      uuid                     not null
        references users
            on delete cascade,
    channel_id   uuid                     not null
        references channels
            on delete cascade,
    last_read_at timestamp with time zone not null,
    unique (user_id, channel_id)
);

alter table readstatus
    owner to discodeit_user;


