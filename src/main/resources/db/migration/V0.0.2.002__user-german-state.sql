alter table user
    add column german_state varchar(255);

update user
set german_state = 'MV'
where german_state is null;