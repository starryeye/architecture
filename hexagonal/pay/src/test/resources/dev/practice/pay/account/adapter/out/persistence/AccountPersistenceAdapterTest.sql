insert into account (id) values (1);

insert into account (id) values (2);

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1, '2022-05-08 08:00:00.0', 1, 1, 2, 500);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (2, '2022-05-08 08:00:00.0', 2, 1, 2, 500);

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (3, '2022-05-09 10:00:00.0', 1, 2, 1, 1000);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (4, '2022-05-09 10:00:00.0', 2, 2, 1, 1000);

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (5, '2023-05-09 09:00:00.0', 1, 1, 2, 1000);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (6, '2023-05-09 09:00:00.0', 2, 1, 2, 1000);

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (7, '2023-05-09 10:00:00.0', 1, 2, 1, 1000);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (8, '2023-05-09 10:00:00.0', 2, 2, 1, 1000);