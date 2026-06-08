create table user_credentials
(
    id         uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    user_id    uuid                     not null,
    password   varchar(300)             not null
);

alter table user_credentials
    add constraint fk_user_credentials_user_id foreign key (user_id) references users (id) on delete cascade;

drop index if exists idx_users_username_password;

alter table users
    drop column password,
    add column role varchar(20) not null;
