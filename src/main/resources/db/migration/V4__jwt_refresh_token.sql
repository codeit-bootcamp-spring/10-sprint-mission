create table jwt_refresh_tokens
(
    id             uuid primary key,
    created_at     timestamp with time zone not null,
    updated_at     timestamp with time zone not null,
    expires_at     timestamp with time zone not null,
    device         varchar(100)             not null,
    user_id        uuid                     not null,
    token          varchar(512)             not null,
    previous_token varchar(512)
);

alter table jwt_refresh_tokens
    add constraint fk_jwt_refresh_tokens_user_id
        foreign key (user_id) references users (id) on delete cascade;

create index idx_jwt_refresh_tokens_token on jwt_refresh_tokens (token);
create index idx_jwt_refresh_tokens_previous_token on jwt_refresh_tokens (previous_token);
