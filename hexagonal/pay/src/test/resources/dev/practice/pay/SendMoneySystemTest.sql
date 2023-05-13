insert into account (id) values (1);
insert into account (id) values (2);

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1001, '2022-05-12 08:00:00.0', 1, 1, 2, 500);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1002, '2022-05-12 08:00:00.0', 2, 1, 2, 500);
-- 1: -500, 2: +500

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1003, '2022-05-13 10:00:00.0', 1, 2, 1, 1000);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1004, '2022-05-13 10:00:00.0', 2, 2, 1, 1000);
-- 1: +500, 2: -500

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1005, '2023-05-13 09:00:00.0', 1, 1, 2, 1000);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1006, '2023-05-13 09:00:00.0', 2, 1, 2, 1000);
-- 1: -500, 2: +500

insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1007, '2023-05-13 10:00:00.0', 1, 2, 1, 1000);
insert into activity (id, created_at, owner_account_id, source_account_id, target_account_id, amount)
values (1008, '2023-05-13 10:00:00.0', 2, 2, 1, 1000);
-- 1: +500, 2: -500