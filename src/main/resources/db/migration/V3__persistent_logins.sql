CREATE TABLE persistent_logins
(
    series    varchar(64) primary key,
    username  uuid                     not null,
    token     varchar(300)             not null,
    last_used timestamp with time zone not null
);

alter table persistent_logins
    add constraint fk_persistent_logins_username foreign key (username) references users (id);
