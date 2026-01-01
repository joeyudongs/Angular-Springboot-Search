-- org 1
insert into user_info (org_id, email, uname, firstname, lastname, is_company_user, created_time)
select
  1,
  ('user' || gs || '@org1.com'),
  ('user' || gs || '@org1.com'),
  ('First' || gs),
  ('Last' || gs),
  true,
  now() - (gs || ' minutes')::interval
from generate_series(1, 1000) gs;

-- org 2
insert into user_info (org_id, email, uname, firstname, lastname, is_company_user, created_time)
select
  2,
  ('user' || gs || '@org2.com'),
  ('user' || gs || '@org2.com'),
  ('First' || gs),
  ('Last' || gs),
  true,
  now() - (gs || ' minutes')::interval
from generate_series(1, 1000) gs;

-- org 3
insert into user_info (org_id, email, uname, firstname, lastname, is_company_user, created_time)
select
  3,
  ('user' || gs || '@org3.com'),
  ('user' || gs || '@org3.com'),
  ('First' || gs),
  ('Last' || gs),
  true,
  now() - (gs || ' minutes')::interval
from generate_series(1, 1000) gs;
