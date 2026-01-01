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

-- -- org_id index
-- create index idx_user_info_org_id on user_info(org_id);

-- -- 常见：email 精确或前缀查询会更快（可选）
-- create index idx_user_info_email on user_info(email);
ß