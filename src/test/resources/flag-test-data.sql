SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE USER;
ALTER TABLE USER
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE FLAG;
ALTER TABLE FLAG
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE FLAG_BAKERY;
ALTER TABLE FLAG_BAKERY
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE bakery;
ALTER TABLE bakery
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE review;
ALTER TABLE review
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE review_comment;
ALTER TABLE review_comment
    ALTER COLUMN ID RESTART WITH 1;

insert into USER (id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image')
;


insert into FLAG (id, created_at, modified_at, color, name, user_id )values
(111,  '2023-01-01', '2023-01-01', 'YELLOW', '가즈아', 111),
(112,  '2023-01-02', '2023-01-01', 'CYAN', 'flag1', 111),
(113,  '2023-01-03', '2023-01-01', 'PINK', 'flag2', 112)
;

insert into FLAG_BAKERY (id, created_at, modified_at, bakery_id, flag_id, user_id )values
(111,  '2023-01-01', '2023-01-01', 111, 111, 111),
(112,  '2023-01-02', '2023-01-01', 112, 112, 111),
(113,  '2023-02-01', '2023-01-01', 113, 112, 111),
(114,  '2023-01-03', '2023-01-01', 113, 113, 112)
;

insert into bakery (id, created_at, modified_at, address, latitude, longitude, name, status  ,image )values
(111,  '2023-01-01', '2023-01-01', '수원', 37.5596080725671, 127.044235133983,'수원빵집','POSTING' , 'image111'),
(112,  '2023-01-02', '2023-01-01', '서울', 37.5596080725632, 127.044235133932,'서울 빵집','POSTING' , 'image'),
(113,  '2023-01-03', '2023-01-01', '제주도', 37.5596080725679, 127.044235133911,'제주도 빵집', 'POSTING', 'image2')
;

insert into review (id, created_at, modified_at, content, is_block, is_delete, is_hide, is_new  ,bakery_id, user_id )values
(111,  '2023-01-01', '2023-01-01', '수원',  'N', 'N', 'N', 'N', 113, 112),
(112,  '2023-01-02', '2023-01-01', '서울',  'N', 'N', 'N', 'N', 113, 112),
(113,  '2023-01-03', '2023-01-01', '제주도', 'N', 'N', 'N', 'N', 113, 112)
;

insert into review_comment (id, created_at, modified_at, content, is_delete, parent_id, review_id, user_id   )values
(111,  '2023-01-01', '2023-01-01', '좋아요 좋아요 ', 'N', NULL, 111,112),
(113,  '2023-01-03', '2023-01-01', '으웩 으웩', 'N',111 , 111, 112)
