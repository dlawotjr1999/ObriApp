-- Flyway 도입 시점(BACKLOG.md #53)의 베이스라인 스키마.
-- Hibernate 메타데이터(엔티티 매핑)로부터 자동 생성 — 손으로 작성하지 않음(정확성 보장).
-- 기존 DB(로컬·운영)는 이미 이 스키마를 갖고 있으므로 baseline-on-migrate로 처리되어
-- 이 스크립트는 실행되지 않고, 새로 생성되는 빈 DB(CI 등)에서만 실행된다.

    create table application (id bigint not null auto_increment, additional_info varchar(255), created_at datetime(6), instrument varchar(255) not null, status enum ('ACCEPTED','CANCELLED','PENDING','REJECTED','REVOKED') not null, version bigint, post_id bigint not null, user_id bigint not null, primary key (id)) engine=InnoDB;

    create table career (id bigint not null auto_increment, contexts varchar(255), organization varchar(255), user_id bigint not null, primary key (id)) engine=InnoDB;

    create table concours (id bigint not null auto_increment, category varchar(255) not null, crawledAt datetime(6) not null, deadline datetime(6) not null, endDate datetime(6) not null, organizer varchar(255) not null, startDate datetime(6) not null, title varchar(255) not null, url varchar(255) not null, primary key (id)) engine=InnoDB;

    create table post (post_id bigint not null auto_increment, category varchar(255) not null, created_at datetime(6), description varchar(2000), event_at datetime(6) not null, location varchar(255) not null, manually_closed bit not null, pay integer not null, region varchar(255) not null, status enum ('CLOSED','OPEN','PARTIALLY_CLOSED') not null, timetable varchar(255) not null, title varchar(255) not null, version bigint, user_id bigint not null, primary key (post_id)) engine=InnoDB;

    create table post_instrument (id bigint not null auto_increment, closed bit not null, confirmed integer not null, instrument varchar(255) not null, people integer not null, version bigint, post_id bigint not null, primary key (id)) engine=InnoDB;

    create table practice_feedback (id bigint not null auto_increment, created_at datetime(6), feedback varchar(255), period_end varchar(255), period_start varchar(255), user_id bigint not null, primary key (id)) engine=InnoDB;

    create table practice_log (id bigint not null auto_increment, content varchar(255), created_at datetime(6), duration integer, log_date date, updated_at datetime(6), user_id bigint not null, primary key (id)) engine=InnoDB;

    create table user (user_id bigint not null auto_increment, created_at datetime(6), email varchar(255), fcm_token varchar(255), firebase_uid varchar(255) not null, instrument varchar(255) not null, is_graduate bit not null, nickname varchar(255) not null, phone_number varchar(255) not null, school varchar(255) not null, school_email varchar(255), school_email_verified bit not null, primary key (user_id)) engine=InnoDB;

    alter table application add constraint UK1m278y6v8jh3b4i9aeetf3dc2 unique (post_id, user_id);

    alter table user add constraint UKob8kqyqqgmefl0aco34akdtpe unique (email);

    alter table user add constraint UKt4a3rh3as7kgr5orl5qmd1x4b unique (firebase_uid);

    alter table user add constraint UK4bgmpi98dylab6qdvf9xyaxu4 unique (phone_number);

    alter table user add constraint UK24kx9bfqv9yg16btcdo76hp9r unique (school_email);

    alter table application add constraint FKtbcejcatqctectw6hhwwbsv97 foreign key (post_id) references post (post_id);

    alter table application add constraint FKldca8xj6lqb3rsqawrowmkqbg foreign key (user_id) references user (user_id);

    alter table career add constraint FKp68p71rciqjtwly9h9auk0vyi foreign key (user_id) references user (user_id);

    alter table post add constraint FK72mt33dhhs48hf9gcqrq4fxte foreign key (user_id) references user (user_id);

    alter table post_instrument add constraint FKe1yjneomywdk551803ljb6sly foreign key (post_id) references post (post_id);

    alter table practice_feedback add constraint FK55lb5vxmaasfui2ar37wi3o2i foreign key (user_id) references user (user_id);

    alter table practice_log add constraint FKtnep7mt5fsvi08es4bdt5q7hc foreign key (user_id) references user (user_id);
