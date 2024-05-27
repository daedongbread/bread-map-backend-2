CREATE TABLE bread_diary_event
(
    diary_id      BIGINT       NOT NULL,
    created_at    datetime     NULL,
    modified_at   datetime     NULL,
    `description` VARCHAR(2000) NOT NULL DEFAULT '',
    state         VARCHAR(255) NOT NULL,
    CONSTRAINT pk_breaddiaryevent PRIMARY KEY (diary_id)
);

ALTER TABLE bread_diary_event
    ADD CONSTRAINT FK_BREADDIARYEVENT_ON_DIARY FOREIGN KEY (diary_id) REFERENCES bread_diary (id);

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

CREATE TABLE user_point_history
(
    id                BIGINT       NOT NULL,
    created_at        datetime     NULL,
    modified_at       datetime     NULL,
    user_id           BIGINT       NOT NULL,
    point             INT          NOT NULL,
    grand_total_point INT          NOT NULL,
    type              VARCHAR(255) NOT NULL,
    `description`     VARCHAR(2000) NOT NULL DEFAULT '',
    target_id         BIGINT       NULL,
    CONSTRAINT pk_userpointhistory PRIMARY KEY (id)
);

ALTER TABLE user_point_history
    ADD CONSTRAINT FK_USERPOINT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);
