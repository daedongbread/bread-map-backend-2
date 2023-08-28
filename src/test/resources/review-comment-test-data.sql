SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE USER;
ALTER TABLE USER
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

TRUNCATE TABLE review_comment_like;
ALTER TABLE review_comment_like
    ALTER COLUMN ID RESTART WITH 1;


insert into USER (is_de_registered,id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(false,111,  '2023-01-01', '2023-01-01', 'USER', 'N',  true, 'N', 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(false,112,  '2023-01-01', '2023-01-01', 'USER', 'N',  true, 'N', 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image')
;


insert into bakery (id, created_at, modified_at, address, latitude, longitude, name, status )values
(111,  '2023-01-01', '2023-01-01', '수원', 37.5596080725671, 127.044235133983,'수원빵집','POSTING'),
(112,  '2023-01-02', '2023-01-01', '서울', 37.5596080725632, 127.044235133932,'서울 빵집','POSTING'),
(113,  '2023-01-03', '2023-01-01', '제주도', 37.5596080725679, 127.044235133911,'제주도 빵집', 'POSTING')
;


insert into review (id, created_at, modified_at, content, is_block, is_delete, is_hide, is_new, bakery_id, user_id   )values
(111,  '2023-01-01', '2023-01-01', '좋아요!', 'N', 'N', 'N','N',111,111),
(112,  '2023-01-02', '2023-01-01', '맛있어요!', 'N', 'N', 'N','N',111,112),
(113,  '2023-01-03', '2023-01-01', '으웩!', 'Y', 'N','N','N',112, 112)
;

insert into review_comment (id, created_at, modified_at, content, is_delete, parent_id, review_id, user_id   )values
(111,  '2023-01-01', '2023-01-01', '좋아요 좋아요 ', 'N', NULL, 111,112),
(113,  '2023-01-03', '2023-01-01', '으웩 으웩', 'N',111 , 111, 112)
