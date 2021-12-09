alter table t_events drop constraint t_events__fk_users_id;
alter table t_events add constraint t_events__fk_users_id foreign key (user_id) references t_users(id) on update cascade on delete cascade;