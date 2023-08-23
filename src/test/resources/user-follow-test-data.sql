SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE USER;
ALTER TABLE USER
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE post;
ALTER TABLE post
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE block_user;
ALTER TABLE block_user
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE comment;
ALTER TABLE comment
    ALTER COLUMN ID RESTART WITH 1;


TRUNCATE TABLE comment_like;
ALTER TABLE comment_like
    ALTER COLUMN ID RESTART WITH 1;

insert into USER (id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image, is_de_registered)values
(111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image', false),
(112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image', true),
(113,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_3333', 'nick_name2332', 'tes33t@apple.com' , 'MALE', 'image', false)
;
