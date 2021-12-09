create table t_users
(
    id bigserial
        constraint t_users_pk
            primary key,
    full_name varchar not null,
    email varchar not null,
    password varchar not null,
    role_id varchar
);