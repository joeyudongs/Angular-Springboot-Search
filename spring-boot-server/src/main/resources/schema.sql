drop table if exists user_info;

create table user_info (
  id              bigserial primary key,
  org_id          bigint not null,
  email           varchar(255) not null,
  uname           varchar(255) not null,
  firstname       varchar(100) not null,
  lastname        varchar(100) not null,
  is_company_user boolean not null default true,
  created_time    timestamptz not null default now()
);

-- Speeds up tenant isolation: quickly narrows search to rows of a single org.
create index idx_user_info_org_id on user_info(org_id);

-- case-insensitive prefix search indexes (match lower(...) in query)
-- Composite index for "org_id + firstname prefix search" (case-insensitive via lower())
create index idx_user_info_org_lower_firstname on user_info(org_id, lower(firstname));
-- Composite index for "org_id + lastname prefix search" (case-insensitive via lower())
create index idx_user_info_org_lower_lastname on user_info(org_id, lower(lastname));


-- Enables fast "contains search" (ILIKE '%abc%') on email using trigram matching
create extension if not exists pg_trgm;
create index idx_user_info_lower_email_trgm
on user_info using gin (lower(email) gin_trgm_ops);

-- -- 常见：email 精确或前缀查询会更快（可选）contains: B-tree几乎用不上
-- create index idx_user_info_email on user_info(email);

-- explain analyze
-- select *
-- from user_info
-- where org_id = 1
--   and lower(firstname) like 'fi%'
-- limit 20;

-- Index Scan using idx_user_info_org_lower_firstname
-- Or Bitmap Index Scan ...