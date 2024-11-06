alter table user
    add column is_archived bit default false;

update user
set is_archived = false
where is_archived is null;