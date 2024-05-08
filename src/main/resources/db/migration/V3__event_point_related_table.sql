DROP TABLE IF EXISTS bread_diary_event;
CREATE TABLE bread_diary_event
(
    diary_id      BIGINT        NOT NULL,
    created_at    datetime      NULL,
    modified_at   datetime      NULL,
    `description` VARCHAR(2000) NOT NULL DEFAULT '',
    state         VARCHAR(255)  NOT NULL,
    CONSTRAINT pk_breaddiaryevent PRIMARY KEY (diary_id)
);

ALTER TABLE bread_diary_event
    ADD CONSTRAINT FK_BREADDIARYEVENT_ON_DIARY FOREIGN KEY (diary_id) REFERENCES bread_diary (id);

DROP TABLE IF EXISTS user_point;
CREATE TABLE user_point
(
    user_id     BIGINT   NOT NULL,
    created_at  datetime NULL,
    modified_at datetime NULL,
    total_point INT      NOT NULL,
    CONSTRAINT pk_userpoint PRIMARY KEY (user_id)
);

ALTER TABLE user_point
    ADD CONSTRAINT FK_USERPOINT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

DROP TABLE IF EXISTS user_point_history;
CREATE TABLE user_point_history
(
    id                BIGINT        NOT NULL,
    created_at        datetime      NULL,
    modified_at       datetime      NULL,
    user_id           BIGINT        NOT NULL,
    point             INT           NOT NULL,
    grand_total_point INT           NOT NULL,
    type              VARCHAR(255)  NOT NULL,
    `description`     VARCHAR(2000) NOT NULL DEFAULT '',
    target_id         BIGINT        NULL,
    CONSTRAINT pk_userpointhistory PRIMARY KEY (id)
);

alter table challenge CHANGE start_datetime start_date_time datetime;
ALTER TABLE challenge CHANGE end_datetime end_date_time datetime;
