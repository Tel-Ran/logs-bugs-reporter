delete from bugs;
delete from artifacts;
delete from programmers;

insert into programmers (id, name, email) values (123, 'Moshe', 'moshe@gmail.com');
insert into artifacts (artifact_id, programmer_id) values ('artifact1', 123);