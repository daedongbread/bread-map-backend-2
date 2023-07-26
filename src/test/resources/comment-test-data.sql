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

insert into comment( id,created_at, user_id, content, post_id, parent_id, is_first_depth, status, target_comment_user_id) values
(111, '2023-01-01', 111, 'content',  222, 0, true, 'ACTIVE', 0),
(112,'2023-01-02', 112, 'content 1', 222, 0, true, 'DELETED', 0),
(113, '2023-01-03',113, 'content 2', 222, 111, false, 'ACTIVE',111 ),
(114, '2023-01-04',113, 'content 2', 222, 111, false, 'ACTIVE', 111),
(115, '2023-01-05',112, 'content 2', 222, 112, false, 'ACTIVE', 112),
(116, '2023-01-06',113, 'content 2', 222, 0, true, 'BLOCKED', 0),
(117, '2023-01-07',111, 'content 2', 222, 116, false, 'ACTIVE', 113),
(118, '2023-01-08',111, 'content 2', 222, 112, false, 'ACTIVE', 112),
(119, '2023-01-09',112, 'content 2', 222, 111, false, 'ACTIVE',111 ),
(120, '2023-02-01', 111, 'content',  223, 0, true, 'ACTIVE',0 ),
(121, '2023-03-01', 111, 'content',  222, 0, true, 'ACTIVE',0 ),
(122, '2022-01-01', 111, 'content',  222, 0, true, 'BLOCKED', 0)
;
insert into block_user (id,from_user_id,to_user_id) values
(111, 111, 113)
;