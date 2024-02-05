create table contract
(
    id                     bigint       not null auto_increment,
    version                bigint       not null,
    end_at                 datetime,
    start_at               datetime     not null,
    hours_per_week         float        not null,
    user_id                bigint       not null,
    working_days           varchar(255) not null,
    vacation_days_per_year integer      not null,
    primary key (id)
) engine=InnoDB;

create table day_item
(
    id      bigint       not null auto_increment,
    version bigint       not null,
    day     datetime     not null,
    user_id bigint       not null,
    type    varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table manual_entry
(
    id      bigint       not null auto_increment,
    version bigint       not null,
    date    datetime     not null,
    user_id bigint       not null,
    type    varchar(255) not null,
    note    varchar(255),
    amount  float        not null,
    primary key (id)
) engine=InnoDB;

create table role
(
    id        bigint       not null auto_increment,
    version   bigint       not null,
    authority varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table user
(
    id               bigint       not null auto_increment,
    version          bigint       not null,
    password_expired bit          not null,
    account_expired  bit          not null,
    firstname        varchar(255),
    username         varchar(255) not null,
    account_locked   bit          not null,
    `password`       varchar(255) not null,
    lastname         varchar(255),
    enabled          bit          not null,
    primary key (id)
) engine=InnoDB;

create table user_role
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
) engine=InnoDB;

create table working_time
(
    id         bigint   not null auto_increment,
    version    bigint   not null,
    end_at     datetime,
    start_at   datetime not null,
    user_id    bigint   not null,
    note       varchar(255),
    break_time float    not null,
    primary key (id)
) engine=InnoDB;

alter table role
    add constraint UK_irsamgnera6angm0prq1kemt2 unique (authority);
alter table user
    add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);
alter table contract
    add constraint FK5s7r1nq49s36ndq7nlri6hxf7 foreign key (user_id) references user (id);
alter table day_item
    add constraint FKfdwg29v6hs1nr42nurdjkpajx foreign key (user_id) references user (id);
alter table manual_entry
    add constraint FKiji62ibgnx5osyhvfvxsqduo6 foreign key (user_id) references user (id);
alter table user_role
    add constraint FK859n2jvi8ivhui0rl0esws6o foreign key (user_id) references user (id);
alter table user_role
    add constraint FKa68196081fvovjhkek5m97n3y foreign key (role_id) references role (id);
alter table working_time
    add constraint FKa81323dhtqc0expspcfboqysh foreign key (user_id) references user (id);
