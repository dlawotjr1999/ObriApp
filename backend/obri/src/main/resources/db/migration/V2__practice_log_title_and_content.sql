-- 연습일지(practice_log) 백엔드 구현(BACKLOG.md, 연습일지 CRUD) — 프론트 요구사항에 맞춰 title 컬럼 추가,
-- log_date·duration을 NOT NULL로, content 길이를 varchar(255)에서 varchar(4000)으로 확장(Post.description과 동일 방식).

alter table practice_log add column title varchar(255) not null;

alter table practice_log modify column log_date date not null;

alter table practice_log modify column duration integer not null;

alter table practice_log modify column content varchar(4000);
