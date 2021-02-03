delete from bugs;
delete from artifacts;
delete from programmers;

insert into programmers (id, name) values (123, 'Moshe');
insert into artifacts (artifact_id, programmer_id) values ('artifact1', 123);