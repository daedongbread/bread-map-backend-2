create table challenge
(
    id             bigint auto_increment not null,
    created_at     datetime null,
    modified_at    datetime null,
    title          varchar(255) not null,
    link_url       varchar(255) null,
    start_datetime datetime     not null,
    end_datetime   datetime     not null,
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