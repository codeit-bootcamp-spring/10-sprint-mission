create table notifications
(
    id          uuid primary key,
    created_at  timestamp with time zone not null,
    receiver_id uuid                     not null,
    title       varchar(255)             not null,
    content     varchar(255)             not null
);

alter table notifications
    add constraint fk_notifications_receiver_id foreign key (receiver_id) references users (id) on delete cascade;

alter table read_statuses
    add column notification_enabled boolean not null default false;
