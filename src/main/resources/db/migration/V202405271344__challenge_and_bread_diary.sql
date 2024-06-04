create table challenge
(
    id             bigint auto_increment not null,
    created_at     datetime null,
    modified_at    datetime null,
    title          varchar(255) not null,
    link_url       varchar(255) null,
    start_date_time datetime     not null,
    end_date_time   datetime     not null,
    available      bit(1)       not null,
    `limit`        int null,
    constraint pk_challenge primary key (id)
);

create table bread_diary
(
    id            bigint auto_increment not null,
    created_at    datetime null,
    modified_at   datetime null,
    user_id       bigint       not null,
    bakery_id     bigint       not null,
    product_name  varchar(255) not null,
    product_price int          not null,
    rating        int          not null,
    constraint pk_breaddiary primary key (id)
);

alter table bread_diary
    add constraint fk_breaddiary_on_bakery foreign key (bakery_id) references bakery (id);

alter table bread_diary
    add constraint fk_breaddiary_on_user foreign key (user_id) references user (id);

create table review_tag
(
    id              bigint auto_increment not null,
    created_at      datetime null,
    modified_at     datetime null,
    review_tag_type varchar(255) null,
    constraint pk_reviewtag primary key (id)
);

create table bakery_tag
(
    id              bigint auto_increment not null,
    created_at      datetime null,
    modified_at     datetime null,
    tag_order       bigint null,
    review_tag_type varchar(255) null,
    is_active       bit(1)       not null,
    `description`   varchar(255) not null,
    constraint pk_bakerytag primary key (id)
);

create table bread_tag
(
    id              bigint auto_increment not null,
    created_at      datetime null,
    modified_at     datetime null,
    tag_order       bigint null,
    review_tag_type varchar(255) null,
    is_active       bit(1)       not null,
    `description`   varchar(255) not null,
    constraint pk_breadtag primary key (id)
);

CREATE TABLE challenge_participant
(
    id           bigint AUTO_INCREMENT NOT NULL,
    created_at   datetime              NULL,
    modified_at  datetime              NULL,
    challenge_id bigint                NOT NULL,
    user_id      bigint                NOT NULL,
    CONSTRAINT pk_challengeparticipant PRIMARY KEY (id)
);

ALTER TABLE challenge_participant
    ADD CONSTRAINT fk_challengeparticipant_on_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (id);

ALTER TABLE challenge_participant
    ADD CONSTRAINT fk_challengeparticipant_on_user FOREIGN KEY (user_id) REFERENCES user (id);
