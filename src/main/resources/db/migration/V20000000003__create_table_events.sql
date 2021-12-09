create table t_events
(
    id bigserial
        constraint t_events__pk
            primary key,
    user_id bigint not null
        constraint t_events__fk_users_id
            references t_users (id)
            on update cascade on delete set null,
    type varchar not null,
    description varchar,
    date date default current_date,
    privacy varchar default 'PUBLIC' not null
);

comment on column t_events.id is 'Primary key';

comment on column t_events.user_id is 'Foreign key';

comment on column t_events.type is 'NOT NULL';

comment on column t_events.date is 'DEFAULT Value - current_date()';

comment on column t_events.privacy is 'PRIVATE or PUBLIC, DEFAULT Value - PUBLIC';