CREATE DATABASE IF NOT EXISTS local_bread_map;

USE local_bread_map;

CREATE TABLE IF NOT EXISTS admin
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    email       varchar(255) NOT NULL,
    password    varchar(255) NOT NULL,
    role_type   varchar(255) NULL,
    CONSTRAINT UK_c0r9atamxvbhjjvy5j8da1kam
        UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS bakery_view
(
    bakery_id  bigint NOT NULL,
    view_date  date   NOT NULL,
    view_count bigint NOT NULL,
    PRIMARY KEY (bakery_id, view_date)
);

CREATE TABLE IF NOT EXISTS carousel_manager
(
    id             bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at     datetime(6)  NULL,
    modified_at    datetime(6)  NULL,
    banner_image   varchar(255) NULL,
    carousel_order int          NOT NULL,
    carousel_type  varchar(255) NULL,
    carouseled     bit          NOT NULL,
    target_id      bigint       NULL
);

CREATE TABLE IF NOT EXISTS category
(
    id            bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at    datetime     NULL,
    modified_at   datetime     NULL,
    category_name varchar(255) NULL
);

CREATE TABLE IF NOT EXISTS feed
(
    feed_type     varchar(31)  NOT NULL,
    id            bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at    datetime     NULL,
    modified_at   datetime     NULL,
    activated     varchar(255) NULL,
    active_time   datetime     NULL,
    conclusion    varchar(255) NULL,
    introduction  varchar(255) NULL,
    sub_title     varchar(255) NULL,
    thumbnail_url varchar(255) NULL,
    admin_id      bigint       NULL,
    category_id   bigint       NULL,
    CONSTRAINT FK53yr92wxtg8wj0gyi2l91un5b
        FOREIGN KEY (admin_id) REFERENCES admin (id),
    CONSTRAINT FKqrkghx9e7x9qr0boho5n3ein5
        FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS curation
(
    likes int    NULL,
    id    bigint NOT NULL
        PRIMARY KEY,
    CONSTRAINT FKaqg3ssfd22ej1mgygp9otdj3l
        FOREIGN KEY (id) REFERENCES feed (id)
);

CREATE TABLE IF NOT EXISTS hot_keyword
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    keyword     varchar(255) NOT NULL,
    ranking     int          NOT NULL
);

CREATE TABLE IF NOT EXISTS image
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    hash_value  varchar(255) NOT NULL,
    CONSTRAINT UK_5ohqp2o3c06h0gt7km0hq0oyb
        UNIQUE (hash_value)
);

CREATE TABLE IF NOT EXISTS landing_feed
(
    redirect_url varchar(255) NULL,
    id           bigint       NOT NULL
        PRIMARY KEY,
    CONSTRAINT FK5shv255erx30e6l8b05i70ftn
        FOREIGN KEY (id) REFERENCES feed (id)
);

CREATE TABLE IF NOT EXISTS subway_station
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    longitude   double       NOT NULL,
    line        varchar(255) NOT NULL,
    latitude    double       NOT NULL,
    name        varchar(255) NOT NULL,
    CONSTRAINT subway_station_uk1
        UNIQUE (line, name)
)
    CHARSET = utf8mb4;

CREATE INDEX subway_station_idx1
    ON subway_station (name);

CREATE INDEX subway_station_idx2
    ON subway_station (longitude, latitude);

CREATE TABLE IF NOT EXISTS user
(
    id                                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                         datetime     NULL,
    modified_at                        datetime     NULL,
    is_alarm_on                        varchar(255) NOT NULL,
    is_block                           varchar(255) NOT NULL,
    is_marketing_info_reception_agreed varchar(255) NOT NULL,
    oauth_id                           varchar(255) NOT NULL,
    oauth_type                         varchar(255) NOT NULL,
    role_type                          varchar(255) NOT NULL,
    email                              varchar(255) NULL,
    gender                             varchar(255) NULL,
    image                              varchar(255) NULL,
    nick_name                          varchar(255) NOT NULL,
    is_de_registered                   bit          NOT NULL,
    last_accessed_at                   datetime     NULL,
    CONSTRAINT UK_d2ia11oqhsynodbsi46m80vfc
        UNIQUE (nick_name)
);

CREATE TABLE IF NOT EXISTS bakery_add_report
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    content     varchar(255) NULL,
    location    varchar(255) NOT NULL,
    name        varchar(255) NOT NULL,
    status      varchar(255) NOT NULL,
    user_id     bigint       NULL,
    CONSTRAINT FKo5jplp6f3ox2mygghym54jddp
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS bakery
(
    id                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at         datetime     NULL,
    modified_at        datetime     NULL,
    address            varchar(255) NOT NULL,
    blogurl            varchar(255) NULL,
    facebookurl        varchar(255) NULL,
    instagramurl       varchar(255) NULL,
    websiteurl         varchar(255) NULL,
    facility_info_list varchar(255) NULL,
    hours              varchar(255) NULL,
    latitude           double       NOT NULL,
    longitude          double       NOT NULL,
    name               varchar(255) NOT NULL,
    phone_number       varchar(255) NULL,
    status             varchar(255) NOT NULL,
    owner_id           bigint       NULL,
    detailed_address   varchar(255) NULL,
    report_id          bigint       NULL,
    check_point        varchar(255) NULL,
    new_bread_time     varchar(255) NULL,
    image              varchar(255) NULL,
    CONSTRAINT FK2twrokiajd2gft5jbxvumt00y
        FOREIGN KEY (owner_id) REFERENCES user (id),
    CONSTRAINT FKkhhqr4m8mfnyv1qfshshalrcu
        FOREIGN KEY (report_id) REFERENCES bakery_add_report (id)
);

CREATE INDEX user_id
    ON bakery_add_report (user_id);

CREATE TABLE IF NOT EXISTS bakery_add_report_image
(
    id                   bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at           datetime     NULL,
    modified_at          datetime     NULL,
    image                varchar(255) NOT NULL,
    is_new               varchar(255) NOT NULL,
    bakery_add_report_id bigint       NULL,
    CONSTRAINT FKn4lu2nydey53yt1n975y1fwco
        FOREIGN KEY (bakery_add_report_id) REFERENCES bakery_add_report (id)
);

CREATE INDEX bakery_add_report_id
    ON bakery_add_report_image (bakery_add_report_id);

CREATE TABLE IF NOT EXISTS bakery_images
(
    bakery_id bigint       NOT NULL,
    images    varchar(255) NULL,
    CONSTRAINT bakery_images_ibfk_1
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON bakery_images (bakery_id);

CREATE TABLE IF NOT EXISTS bakery_report_image
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    image       varchar(255) NOT NULL,
    is_new      varchar(255) NOT NULL,
    bakery_id   bigint       NULL,
    user_id     bigint       NULL,
    CONSTRAINT FKem06ax8rxdlfeyk8bbpwe2u1
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKq1s5kyaa2y3sol7rj24riyp98
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON bakery_report_image (bakery_id);

CREATE INDEX user_id
    ON bakery_report_image (user_id);

CREATE TABLE IF NOT EXISTS bakery_update_report
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    content     varchar(255) NOT NULL,
    is_change   varchar(255) NOT NULL,
    is_new      varchar(255) NOT NULL,
    reason      varchar(255) NOT NULL,
    bakery_id   bigint       NULL,
    user_id     bigint       NULL,
    CONSTRAINT FK28q9vvek2ub7hjlpx60bk8qp0
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKeai9h5m6wo1nde4jpbbfco18r
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON bakery_update_report (bakery_id);

CREATE INDEX user_id
    ON bakery_update_report (user_id);

CREATE TABLE IF NOT EXISTS bakery_update_report_image
(
    id                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at              datetime     NULL,
    modified_at             datetime     NULL,
    image                   varchar(255) NOT NULL,
    bakery_id               bigint       NULL,
    bakery_update_report_id bigint       NULL,
    CONSTRAINT FKamd97vibbg70i4fh84kcckqp4
        FOREIGN KEY (bakery_id) REFERENCES bakery (id),
    CONSTRAINT FKmpclfck1ptd6ltawj06c0v0qq
        FOREIGN KEY (bakery_update_report_id) REFERENCES bakery_update_report (id)
);

CREATE INDEX bakery_id
    ON bakery_update_report_image (bakery_id);

CREATE INDEX bakery_update_report_id
    ON bakery_update_report_image (bakery_update_report_id);

CREATE TABLE IF NOT EXISTS block_user
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at   datetime NULL,
    modified_at  datetime NULL,
    from_user_id bigint   NOT NULL,
    to_user_id   bigint   NOT NULL,
    CONSTRAINT FKb57hotub3jpnlujs9bn3ydxn6
        FOREIGN KEY (from_user_id) REFERENCES user (id),
    CONSTRAINT FKha8i9ymbogylv9rokrsqkq49a
        FOREIGN KEY (to_user_id) REFERENCES user (id)
);

CREATE INDEX FKha8i9ymbogylv9rto_user_idokrsqkq49a
    ON block_user (to_user_id);

CREATE INDEX from_user_id
    ON block_user (from_user_id);

CREATE TABLE IF NOT EXISTS comment
(
    id                     bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at             datetime     NULL,
    modified_at            datetime     NULL,
    content                varchar(500) NOT NULL,
    is_first_depth         bit          NOT NULL,
    parent_id              bigint       NOT NULL,
    post_id                bigint       NULL,
    status                 varchar(255) NOT NULL,
    target_comment_user_id bigint       NOT NULL,
    user_id                bigint       NULL,
    post_topic             varchar(255) NOT NULL,
    CONSTRAINT FK8kcum44fvpupyw6f5baccx25c
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE INDEX FKs1slvnkuemjsq2kj4h3vhx7i1
    ON comment (post_id);

CREATE TABLE IF NOT EXISTS comment_like
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    comment_id  bigint   NULL,
    user_id     bigint   NULL,
    CONSTRAINT FK6arwb0j7by23pw04ljdtxq4p5
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKqlv8phl1ibeh0efv4dbn3720p
        FOREIGN KEY (comment_id) REFERENCES comment (id)
);

CREATE TABLE IF NOT EXISTS curation_bakery
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    product_id  bigint       NULL,
    reason      varchar(255) NULL,
    bakery_id   bigint       NULL,
    curation_id bigint       NULL,
    CONSTRAINT FK10sglrn1f4qmcax2fxnfw4sf2
        FOREIGN KEY (curation_id) REFERENCES curation (id),
    CONSTRAINT FKen1itydno7hpq9l5up6909pnt
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE TABLE IF NOT EXISTS flag
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    color       varchar(255) NOT NULL,
    name        varchar(255) NOT NULL,
    user_id     bigint       NOT NULL,
    CONSTRAINT FK9lijb9byj9yxnmjmdpw1hj1cw
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE INDEX user_id
    ON flag (user_id);

CREATE TABLE IF NOT EXISTS flag_bakery
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    bakery_id   bigint   NOT NULL,
    flag_id     bigint   NOT NULL,
    user_id     bigint   NOT NULL,
    CONSTRAINT FK1a89cyfq8ler0u8mmedhcjfox
        FOREIGN KEY (flag_id) REFERENCES flag (id),
    CONSTRAINT FKh9i1y1n96qd5qg7r3rlthe2u7
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKmyhwtuds3h2u91vak951jmuif
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON flag_bakery (bakery_id);

CREATE INDEX create_date_index
    ON flag_bakery (created_at);

CREATE INDEX flag_id
    ON flag_bakery (flag_id);

CREATE INDEX user_id
    ON flag_bakery (user_id);

CREATE TABLE IF NOT EXISTS follow
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at   datetime NULL,
    modified_at  datetime NULL,
    from_user_id bigint   NOT NULL,
    to_user_id   bigint   NOT NULL,
    CONSTRAINT FKbo8snnjqnckmjhm2d71j3bc84
        FOREIGN KEY (to_user_id) REFERENCES user (id),
    CONSTRAINT FKepp5qc1696afiyipw8jhk6et7
        FOREIGN KEY (from_user_id) REFERENCES user (id)
);

CREATE INDEX from_user_id
    ON follow (from_user_id);

CREATE INDEX to_user_id
    ON follow (to_user_id);

CREATE TABLE IF NOT EXISTS likes
(
    id                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at              datetime NULL,
    modified_at             datetime NULL,
    maximum_feed_like_count int      NOT NULL,
    unlike_status_count     int      NOT NULL,
    count                   int      NOT NULL,
    feed_id                 bigint   NULL,
    user_id                 bigint   NULL,
    CONSTRAINT FK4hl3gvqb5ac0te3eldlfw4525
        FOREIGN KEY (feed_id) REFERENCES curation (id),
    CONSTRAINT FKi2wo4dyk4rok7v4kak8sgkwx0
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS notice
(
    id             bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at     datetime     NULL,
    modified_at    datetime     NULL,
    content        varchar(255) NULL,
    content_id     bigint       NULL,
    sub_content_id bigint       NULL,
    type           varchar(255) NOT NULL,
    user_id        bigint       NOT NULL,
    title          varchar(255) NOT NULL,
    content_param  varchar(255) NULL,
    extra_param    varchar(255) NULL,
    CONSTRAINT FKcvf4mh5se36inrxn7xlh2brfv
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE INDEX user_id
    ON notice (user_id);

CREATE TABLE IF NOT EXISTS notice_token
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at   datetime     NULL,
    modified_at  datetime     NULL,
    device_token varchar(255) NOT NULL,
    user_id      bigint       NOT NULL,
    CONSTRAINT FK4cnol9gk77v0j2qmllt3q7137
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE INDEX user_id
    ON notice_token (user_id);

CREATE TABLE IF NOT EXISTS post
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    content     varchar(500) NOT NULL,
    is_block    varchar(255) NOT NULL,
    is_delete   varchar(255) NOT NULL,
    is_hide     varchar(255) NOT NULL,
    post_topic  varchar(255) NOT NULL,
    title       varchar(100) NOT NULL,
    user_id     bigint       NULL,
    CONSTRAINT FK72mt33dhhs48hf9gcqrq4fxte
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS post_image
(
    id            bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at    datetime     NULL,
    modified_at   datetime     NULL,
    image         varchar(255) NOT NULL,
    is_registered varchar(255) NOT NULL,
    post_id       bigint       NOT NULL,
    CONSTRAINT FKsip7qv57jw2fw50g97t16nrjr
        FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE IF NOT EXISTS post_like
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    post_id     bigint   NULL,
    user_id     bigint   NULL,
    CONSTRAINT FKhuh7nn7libqf645su27ytx21m
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKj7iy0k7n3d0vkh8o7ibjna884
        FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE IF NOT EXISTS post_manager_mapper
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    is_fixed    bit      NOT NULL,
    is_posted   bit      NOT NULL,
    post_id     bigint   NULL,
    CONSTRAINT FKb4dwgvsh0mi2gn7vjqhr983oi
        FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE IF NOT EXISTS product
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at   datetime     NULL,
    modified_at  datetime     NULL,
    image        varchar(255) NULL,
    is_true      varchar(255) NOT NULL,
    name         varchar(255) NOT NULL,
    price        varchar(255) NOT NULL,
    product_type varchar(255) NOT NULL,
    bakery_id    bigint       NULL,
    CONSTRAINT FKayf8oclp5i0bsoxq68y8pn8js
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON product (bakery_id);

CREATE TABLE IF NOT EXISTS product_add_report
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    is_new      varchar(255) NOT NULL,
    name        varchar(255) NOT NULL,
    price       varchar(255) NOT NULL,
    bakery_id   bigint       NULL,
    user_id     bigint       NULL,
    CONSTRAINT FKim2cmg8wxqcp8y5cjdoomci7w
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKpsoknyq3ky074nrdovnkmjghw
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON product_add_report (bakery_id);

CREATE INDEX user_id
    ON product_add_report (user_id);

CREATE TABLE IF NOT EXISTS product_add_report_image
(
    id                    bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at            datetime     NULL,
    modified_at           datetime     NULL,
    image                 varchar(255) NOT NULL,
    is_new                varchar(255) NOT NULL,
    is_registered         varchar(255) NOT NULL,
    bakery_id             bigint       NULL,
    product_add_report_id bigint       NULL,
    CONSTRAINT FKni0jmnv11mwt39hl3grej9srl
        FOREIGN KEY (bakery_id) REFERENCES bakery (id),
    CONSTRAINT FKwmc6igplls9jgcqoqx40q9gb
        FOREIGN KEY (product_add_report_id) REFERENCES product_add_report (id)
);

CREATE INDEX bakery_id
    ON product_add_report_image (bakery_id);

CREATE INDEX product_add_report_id
    ON product_add_report_image (product_add_report_id);

CREATE TABLE IF NOT EXISTS report
(
    id            bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at    datetime     NULL,
    modified_at   datetime     NULL,
    content       varchar(255) NULL,
    post_id       bigint       NOT NULL,
    report_reason varchar(255) NOT NULL,
    report_type   varchar(255) NOT NULL,
    reporter_id   bigint       NULL,
    CONSTRAINT FKndpjl61ubcm2tkf7ml1ynq13t
        FOREIGN KEY (reporter_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS review
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    content     varchar(200) NOT NULL,
    is_block    varchar(255) NOT NULL,
    is_new      varchar(255) NOT NULL,
    bakery_id   bigint       NOT NULL,
    user_id     bigint       NOT NULL,
    is_delete   varchar(255) NOT NULL,
    is_hide     varchar(255) NOT NULL,
    CONSTRAINT FKiyf57dy48lyiftdrf7y87rnxi
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKl4t7vnn2bqf00geuyurl1g342
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON review (bakery_id);

CREATE INDEX user_id
    ON review (user_id);

CREATE TABLE IF NOT EXISTS review_comment
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    content     varchar(255) NOT NULL,
    is_delete   varchar(255) NOT NULL,
    parent_id   bigint       NULL,
    review_id   bigint       NOT NULL,
    user_id     bigint       NOT NULL,
    CONSTRAINT FK336skpbimvangdiji5g0sxlf1
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FK9j7pkcpuestrwjre1a1902biu
        FOREIGN KEY (review_id) REFERENCES review (id),
    CONSTRAINT FKpcp7njr5bif01glg7tcm07qhv
        FOREIGN KEY (parent_id) REFERENCES review_comment (id)
);

CREATE INDEX parent_id
    ON review_comment (parent_id);

CREATE INDEX review_id
    ON review_comment (review_id);

CREATE INDEX user_id
    ON review_comment (user_id);

CREATE TABLE IF NOT EXISTS review_comment_like
(
    id                bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at        datetime NULL,
    modified_at       datetime NULL,
    review_comment_id bigint   NOT NULL,
    user_id           bigint   NOT NULL,
    CONSTRAINT FKky15iemwx70691w2ei9e7ijhx
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FKrcox1bv7k3wrfqc5a2d659noo
        FOREIGN KEY (review_comment_id) REFERENCES review_comment (id)
);

CREATE INDEX review_comment_id
    ON review_comment_like (review_comment_id);

CREATE INDEX user_id
    ON review_comment_like (user_id);

CREATE TABLE IF NOT EXISTS review_image
(
    id            bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at    datetime     NULL,
    modified_at   datetime     NULL,
    image         varchar(255) NOT NULL,
    is_registered varchar(255) NOT NULL,
    is_new        varchar(255) NOT NULL,
    bakery_id     bigint       NOT NULL,
    review_id     bigint       NOT NULL,
    CONSTRAINT FK16wp089tx9nm0obc217gvdd6l
        FOREIGN KEY (review_id) REFERENCES review (id),
    CONSTRAINT FKk17imn41xwjvvjsq7ej3l1u6j
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);

CREATE INDEX bakery_id
    ON review_image (bakery_id);

CREATE INDEX review_id
    ON review_image (review_id);

CREATE TABLE IF NOT EXISTS review_like
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    review_id   bigint   NOT NULL,
    user_id     bigint   NOT NULL,
    CONSTRAINT FK68am9vk1s1e8n1v873meqkk0k
        FOREIGN KEY (review_id) REFERENCES review (id),
    CONSTRAINT FKq4l36vpqal6v4ehikh67g8e49
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE INDEX review_id
    ON review_like (review_id);

CREATE INDEX user_id
    ON review_like (user_id);

CREATE TABLE IF NOT EXISTS review_product_rating
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    rating      bigint   NOT NULL,
    bakery_id   bigint   NOT NULL,
    product_id  bigint   NOT NULL,
    review_id   bigint   NOT NULL,
    user_id     bigint   NULL,
    CONSTRAINT FK6nnyqptw5skuq2blxa5r45750
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FK8j4w42wsbjpniqr80d6376qr6
        FOREIGN KEY (bakery_id) REFERENCES bakery (id),
    CONSTRAINT FKi097r5u0225bsjupe1b16syu0
        FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT FKnshfwpmd0yhmprnhdb4w6p5h5
        FOREIGN KEY (review_id) REFERENCES review (id)
);

CREATE INDEX bakery_id
    ON review_product_rating (bakery_id);

CREATE INDEX product_id
    ON review_product_rating (product_id);

CREATE INDEX review_id
    ON review_product_rating (review_id);

CREATE INDEX user_id
    ON review_product_rating (user_id);

CREATE TABLE IF NOT EXISTS review_report
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NULL,
    modified_at datetime     NULL,
    content     varchar(255) NULL,
    is_block    varchar(255) NOT NULL,
    reason      varchar(255) NOT NULL,
    user_id     bigint       NOT NULL,
    review_id   bigint       NOT NULL,
    CONSTRAINT FK2o5s8o0eoa07bke797x37haym
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FK5m3i8486vomorui2jcgh95mo2
        FOREIGN KEY (review_id) REFERENCES review (id)
);

CREATE INDEX review_id
    ON review_report (review_id);

CREATE INDEX user_id
    ON review_report (user_id);

CREATE TABLE IF NOT EXISTS review_view
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime NULL,
    modified_at datetime NULL,
    view_num    int      NOT NULL,
    review_id   bigint   NULL,
    CONSTRAINT FK97tqqu31mjjedpkoisxhw7h95
        FOREIGN KEY (review_id) REFERENCES review (id)
);

CREATE INDEX review_id
    ON review_view (review_id);

CREATE TABLE IF NOT EXISTS scored_bakery
(
    id              bigint AUTO_INCREMENT
        PRIMARY KEY,
    flag_count      bigint NULL,
    total_score     double NOT NULL,
    bakery_id       bigint NULL,
    calculated_date date   NULL,
    bakery_rank     int    NULL,
    view_count      bigint NOT NULL,
    CONSTRAINT FK7serkwyotx7od9dwm8jfsd1pd
        FOREIGN KEY (bakery_id) REFERENCES bakery (id)
);
