create table binary_contents
(
    created_at   timestamp(6) with time zone not null,
    size         bigint                      not null,
    id           uuid                        not null
        primary key,
    content_type varchar(255),
    file_name    varchar(255)
);

alter table binary_contents
    owner to discodeit_user;

create table channels
(
    type        smallint
        constraint channels_type_check
            check ((type >= 0) AND (type <= 1)),
    created_at  timestamp(6) with time zone not null,
    updated_at  timestamp(6) with time zone not null,
    id          uuid                        not null
        primary key,
    description varchar(255),
    name        varchar(255)
);

alter table channels
    owner to discodeit_user;

create table users
(
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    id         uuid                        not null
        primary key,
    profile_id uuid
        unique
        constraint fktbudycgrip49xdptogmhfqnso
            references binary_contents,
    email      varchar(255)                not null
        unique,
    password   varchar(255)                not null,
    username   varchar(255)                not null
        unique
);

alter table users
    owner to discodeit_user;

create table messages
(
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    channel_id uuid
        constraint fk3u3ckbhwq9se1cmopk2pq05b2
            references channels,
    id         uuid                        not null
        primary key,
    user_id    uuid
        constraint fk_messages_user
            references users
            on delete set null,
    content    varchar(255)                not null
);

alter table messages
    owner to discodeit_user;

create table message_attachments
(
    attachment_id uuid not null
        constraint fksd1m8rb8jcpbcnb7rpdue7ctc
            references binary_contents,
    message_id    uuid not null
        constraint fkj7twd218e2gqw9cmlhwvo1rth
            references messages
);

alter table message_attachments
    owner to discodeit_user;

create table read_statuses
(
    created_at   timestamp(6) with time zone not null,
    last_read_at timestamp(6) with time zone not null,
    updated_at   timestamp(6) with time zone not null,
    channel_id   uuid                        not null
        constraint fk_read_statuses_channel
            references channels
            on delete cascade,
    id           uuid                        not null
        primary key,
    user_id      uuid                        not null
        constraint fk_read_statuses_user
            references users
            on delete cascade,
    constraint uk_read_statuses_user_channel
        unique (user_id, channel_id)
);

alter table read_statuses
    owner to discodeit_user;

create table user_statuses
(
    created_at     timestamp(6) with time zone not null,
    last_active_at timestamp(6) with time zone,
    updated_at     timestamp(6) with time zone not null,
    id             uuid                        not null
        primary key,
    user_id        uuid                        not null
        unique
        constraint fk4lfl3ei2ubchgcxrrpo3pw4mm
            references users
);

alter table user_statuses
    owner to discodeit_user;


