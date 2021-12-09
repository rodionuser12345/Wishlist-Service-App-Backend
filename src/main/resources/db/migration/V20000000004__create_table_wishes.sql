create table t_wishes
(
    id bigserial
        constraint t_wishes_pk
            primary key,
    event_id bigint
        constraint t_wishes__fk_events_id
            references t_events (id)
            on update cascade on delete set null,
    title varchar not null,
    description varchar default 'Empty',
    image varchar default 'Empty'
);

comment on column t_wishes.id is 'PRIMARY KEY';

comment on column t_wishes.event_id is 'FOREIGN KEY';

comment on column t_wishes.description is 'DEFAULT VALUE - Empty';

comment on column t_wishes.image is 'DEFAULT VALUE - Empty';
