create table locked_month
(
    id      bigint  not null auto_increment,
    version bigint  not null,
    month   integer not null,
    user_id bigint  not null,
    year    integer not null,
    primary key (id)
) engine=InnoDB;

alter table locked_month
    add constraint FKh1s7hnqkd6wjn5l8io2frt4o6 foreign key (user_id) references user (id);