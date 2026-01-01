insert into user_info (org_id, email, uname, firstname, lastname, is_company_user, created_time)
select
  1 as org_id,
  ('user' || gs || '@example.com') as email,
  ('user' || gs || '@example.com') as uname,
  ('First' || gs) as firstname,
  ('Last' || gs) as lastname,
  true as is_company_user,
  now() - (gs || ' minutes')::interval as created_time
from generate_series(1, 1000) gs;
