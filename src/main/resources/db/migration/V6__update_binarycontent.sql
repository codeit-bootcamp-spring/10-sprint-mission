alter table binary_contents
    add column updated_at timestamp with time zone,
    add column status     varchar(20) not null default 'SUCCESS';

alter table binary_contents
    add constraint ck_binary_contents_status check (status in ('PROCESSING', 'SUCCESS', 'FAILED', 'DELETED'));
