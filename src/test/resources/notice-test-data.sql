SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE USER;
ALTER TABLE USER
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE NOTICE;
ALTER TABLE NOTICE
    ALTER COLUMN ID RESTART WITH 1;


insert into USER (is_de_registered,id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(false, 111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(false, 112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image')
;


insert into NOTICE (id, created_at, modified_at, content, content_id, type, from_user_id ,user_id )values
(111,  '2023-01-01', '2023-01-01', 'content-test1', 1111, 'FOLLOW', 111, 111),
(112,  '2023-01-02', '2023-01-01', 'content-test2', 1111, 'REVIEW_COMMENT', 111, 111),
(113,  '2023-01-03', '2023-01-01', 'content-test3', 1111, 'REVIEW_LIKE', 111, 111),
(114,  '2023-01-04', '2023-01-01', 'content-test4', 1111, 'RECOMMENT', 111, 111),
(115,  '2023-01-05', '2023-01-01', 'content-test5', 1111, 'REVIEW_COMMENT_LIKE', 111, 111),
(116,  '2023-01-06', '2023-01-01', 'content-test6', 1111, 'ADD_BAKERY', 111, 111),
(117,  '2023-01-07', '2023-01-01', 'content-test7', 1111, 'ADD_PRODUCT', 111, 111),
(118,  '2023-01-08', '2023-01-01', 'content-test8', 1111, 'FLAG_BAKERY_CHANGE', 111, 111),
(119,  '2023-01-09', '2023-01-01', 'content-test9', 1111, 'FLAG_BAKERY_ADMIN_NOTICE', 111, 111)
;

