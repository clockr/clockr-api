create table token
(
    id          bigint       not null auto_increment,
    version     bigint       not null,
    valid_until datetime,
    user_id     bigint,
    type        varchar(255) not null,
    valid_from  datetime     not null,
    identifier  varchar(255) not null,
    primary key (id)
) engine=InnoDB;

alter table token
    add constraint FKe32ek7ixanakfqsdaokm4q9y2 foreign key (user_id) references user (id);