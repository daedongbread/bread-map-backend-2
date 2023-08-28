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

insert into USER (is_de_registered,id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(false, 111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(false, 112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image')
;


insert into FLAG (id, created_at, modified_at, color, name, user_id )values
(100,  '2023-01-01', '2023-01-01', 'YELLOW', '가고싶어요', 111),
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

insert into review_product_rating (id, created_at, modified_at, rating, bakery_id, product_id, review_id, user_id   )values
(112,  '2023-01-01', '2023-01-01', 4, 113, 113, 111,112),
(113,  '2023-01-03', '2023-01-01', 4, 113, 114 , 112, 112),
(114,  '2023-01-03', '2023-01-01', 4, 113, 114 , 111, 112),
(115,  '2023-01-03', '2023-01-01', 4, 113, 114 , 111, 112);



insert into review_comment (id, created_at, modified_at, content, is_delete, parent_id, review_id, user_id   )values
(111,  '2023-01-01', '2023-01-01', '좋아요 좋아요 ', 'N', NULL, 111,112),
(113,  '2023-01-03', '2023-01-01', '으웩 으웩', 'N',111 , 111, 112);


insert into product (id, created_at, modified_at, image, is_true, name, price, product_type,  bakery_id )values
(111,  '2023-01-01', '2023-01-01', '빵이미지 1', 'Y', '빵빵빵', '1000','BREAD', 111),
(113,  '2023-01-03', '2023-01-01', '빵 이미지 2', 'Y', '무슨빵?' , '2000', 'BREAD',113),
(114,  '2023-01-03', '2023-01-01', '빵 이미지 3', 'Y', '소금빵?' , '3000','BREAD', 113);





