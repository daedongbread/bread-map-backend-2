SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE USER;
ALTER TABLE USER
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE post;
ALTER TABLE post
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE post_image;
ALTER TABLE post_image
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE post_like;
ALTER TABLE post_like
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

TRUNCATE TABLE follow;
ALTER TABLE follow
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE post_manager_mapper;
ALTER TABLE post_manager_mapper
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE block_user;
ALTER TABLE block_user
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE comment;
ALTER TABLE comment
    ALTER COLUMN ID RESTART WITH 1;




insert into USER (id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image'),
(113,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_3333', 'nick_name2332', 'tes33t@apple.com' , 'MALE', 'image')
;

insert into post(id ,created_at ,modified_at ,content ,is_block ,is_delete ,is_hide ,post_topic ,title ,user_id) values
(222, '2023-01-01','2023-01-01','test 222 content',false,false,false,'BREAD_STORY','test title', 112),
(223, '2023-01-02','2023-01-01','test 333 content',false,false,false,'BREAD_STORY','test title 223', 113),
(224, '2023-01-03','2023-01-01','test 444 content',false,false,false,'EVENT','test title event', 112),
(225, '2023-01-04','2023-01-01','test 555 content',false,false,false,'EVENT','test title event', 113)
;

insert into post_image (id, created_at, modified_at, image, is_registered, post_id)values
(222, '2023-01-01', '2023-01-01', 'iamge 222 1', true, 222),
(223, '2023-01-01', '2023-01-01', 'iamge 222 2', true, 222)
;

insert into post_like(	id,post_id,user_id,created_at)values
(222, 222, 111, '2023-07-23'),
(224, 223, 111, '2023-07-23'),
(223, 222, 112, '2023-07-23');


insert into bakery (id, created_at, modified_at, address, latitude, longitude, name, status , image )values
(111,  '2023-01-01', '2023-01-01', '수원', 37.5596080725671, 127.044235133983,'수원빵집','POSTING', 'image'),
(112,  '2023-01-02', '2023-01-01', '서울', 37.5596080725632, 127.044235133932,'서울 빵집','POSTING', 'image3'),
(113,  '2023-01-03', '2023-01-01', '제주도', 37.5596080725679, 127.044235133911,'제주도 빵집', 'POSTING', 'image2')
;


insert into review (id, created_at, modified_at, content, is_block, is_delete, is_hide, is_new, bakery_id, user_id   )values
(111,  '2023-07-23', '2023-01-01', '좋아요!', 'N', 'N', 'N','N',111,111),
(112,  '2023-01-02', '2023-01-01', '맛있어요!', 'N', 'N', 'N','N',111,113),
(113,  '2023-01-03', '2023-01-01', '으웩!', 'Y', 'N','N','N',112, 112)
;

insert into review_comment (id, created_at, modified_at, content, is_delete, parent_id, review_id, user_id   )values
(111,  '2023-07-23', '2023-01-01', '좋아요 좋아요 ', 'N', NULL, 111,112),
(113,  '2023-01-03', '2023-01-01', '으웩 으웩', 'N',111 , 111, 112)
;

INSERT INTO follow (id, created_at, modified_at, from_user_id, to_user_id) VALUES
(1, '2023-05-01 15:39:30', '2023-05-01 15:39:30', 111, 112),
(2, '2023-05-02 10:13:19', '2023-05-02 10:13:19', 113, 112),
(3, '2023-05-02 12:16:30', '2023-05-02 12:16:30', 112, 111);

insert into comment( id, user_id, content,is_block, is_hide, is_delete, post_id, parent_id) values
(111, 111, 'content', false, false, false, 222, 0),
(112, 112, 'content 1', false, false, false, 222, 111),
(113, 113, 'content 2', false, false, false, 222, 111)
;

insert into post_manager_mapper (id,created_at , modified_at ,is_carousal,is_fixed, is_processed,post_id )values
(111, '2023-01-01','2023-01-01',false, true, true, 224 ),
(112, '2023-01-01','2023-01-01',true, false, true, 225 )
;


insert into block_user (id,from_user_id,to_user_id) values
(111, 111, 113)
    ;


