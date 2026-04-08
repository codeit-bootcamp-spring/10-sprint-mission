drop domain if exists created_at cascade;
drop domain if exists updated_at cascade;
drop table if exists users cascade;
drop table if exists channels cascade;
drop table if exists messages cascade;
drop table if exists binary_contents cascade;
drop table if exists read_statuses cascade;
drop table if exists message_attachments cascade;
drop table if exists user_statuses cascade;

create domain created_at as timestamp with time zone not null;
create domain updated_at as timestamp with time zone;

create table users
(
    id         uuid primary key,
    created_at created_at,
    updated_at updated_at,
    username   varchar(50)  not null,
    email      varchar(100) not null,
    password   varchar(60)  not null,
    profile_id uuid
);

create index idx_users_username_password ON users(username, password);

create table channels
(
    id          uuid primary key,
    created_at  created_at,
    updated_at  updated_at,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) not null
);

create table messages
(
    id         uuid primary key,
    created_at created_at,
    updated_at updated_at,
    content    text,
    channel_id uuid not null,
    author_id  uuid not null
);
create index idx_messages on messages (channel_id, created_at);

create table binary_contents
(
    id           uuid primary key,
    created_at   created_at,
    file_name    varchar(255) not null,
    size         bigint       not null,
    content_type varchar(100) not null,
    bytes        bytea        not null
);

create table user_statuses
(
    id             uuid primary key,
    created_at     created_at,
    updated_at     updated_at,
    user_id        uuid not null,
    last_active_at created_at
);

create table read_statuses
(
    id           uuid primary key,
    created_at   created_at,
    updated_at   updated_at,
    user_id      uuid not null,
    channel_id   uuid not null,
    last_read_at created_at
);

create table message_attachments
(
    message_id    uuid not null,
    attachment_id uuid not null
);

alter table users
    add constraint fk_users_profile_id foreign key (profile_id) references binary_contents (id),
    add constraint uk_users_username unique (username),
    add constraint uk_users_email unique (email);
alter table channels
    add constraint ck_channels_type check ( type in ('PUBLIC', 'PRIVATE') );
alter table messages
    add constraint fk_messages_channel_id foreign key (channel_id) references channels (id) on delete cascade,
    add constraint fk_messages_author_id foreign key (author_id) references users (id) on delete set null;
alter table user_statuses
    add constraint fk_user_statuses_user_id foreign key (user_id) references users (id) on delete cascade,
    add constraint uk_user_statuses_user_id unique (user_id);
alter table read_statuses
    add constraint fk_read_statuses_user_id foreign key (user_id) references users (id) on delete cascade,
    add constraint fk_read_statuses_channel_id foreign key (channel_id) references channels (id) on delete cascade,
    add constraint uk_read_statuses_user_channel_id unique (user_id, channel_id);
alter table message_attachments
    add constraint fk_message_attachments_message_id foreign key (message_id) references messages (id) on delete cascade,
    add constraint fk_message_attachments_attachment_id foreign key (attachment_id) references binary_contents (id) on delete cascade;
