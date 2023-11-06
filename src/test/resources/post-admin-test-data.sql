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


TRUNCATE TABLE admin;
ALTER TABLE admin
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE carousel_manager;
ALTER TABLE carousel_manager
    ALTER COLUMN ID RESTART WITH 1;

insert into admin (id,created_at,modified_at,email,password,role_type) values
    (111,  '2023-01-01', '2023-01-01', 'admin@email.com', 'test-password', 'ADMIN' )
;

insert into USER (is_de_registered,id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(false, 999,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(false, 112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test@apple.com' , 'MALE', 'image'),
(false, 113,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_3333', 'nick_name2332', 'tes33t@apple.com' , 'MALE', 'image')
;

insert into post(id ,created_at ,modified_at ,content ,is_block ,is_delete ,is_hide ,post_topic ,title ,user_id) values
(222, '2023-01-01','2023-01-01','test 222 content',false,false,false,'BREAD_STORY','test title', 112),
(223, '2023-01-02','2023-01-01','test 333 content',false,false,false,'BREAD_STORY','test title 223', 113),
(224, '2023-01-03','2023-01-01','test 1  content',false,false,false,'EVENT','test title1 event', 112),
(225, '2023-03-04','2023-01-01','test 2  content',false,false,false,'EVENT','test title2 event', 113),
(226, '2023-01-04','2023-01-01','test 3  content',false,false,false,'EVENT','test title3 event', 113),
(227, '2023-02-04','2023-01-01','test 4  content',false,false,false,'EVENT','test title4 event', 113),
(228, '2023-01-04','2023-01-01','test 5  content',false,false,false,'EVENT','test title5 event', 113),
(229, '2023-12-04','2023-01-01','test 6  content',false,false,false,'EVENT','test title6 event', 113),
(230, '2023-11-04','2023-01-01','test 7  content',false,false,false,'EVENT','test title7 event', 113),
(231, '2023-10-04','2023-01-01','test 8  content',false,false,false,'EVENT','test title8 event', 113),
(232, '2023-09-04','2023-01-01','test 9  content',false,false,false,'EVENT','test title9 event', 113),
(233, '2023-08-04','2023-01-01','test 10 content',false,false,false,'EVENT','test title10 event', 113),
(234, '2023-07-04','2023-01-01','test 11 content',false,false,false,'EVENT','test title11 event', 113),
(235, '2023-06-04','2023-01-01','test 12 content',false,false,false,'EVENT','test title12 event', 113),
(236, '2023-05-04','2023-01-01','test 13 content',false,false,false,'EVENT','test title13 event', 113),
(237, '2023-04-04','2023-01-01','test 14 content',false,false,false,'EVENT','test title14 event', 113),
(238, '2023-03-30','2023-01-01','test 15 content',false,false,false,'EVENT','test title15 event', 113),
(239, '2023-02-04','2023-01-01','test 16 content',false,false,false,'EVENT','test title16 event', 113),
(240, '2023-01-10','2023-01-01','test 17 content',false,false,false,'EVENT','test title17 event', 113)
;

insert into post_image (id, created_at, modified_at, image, is_registered, post_id)values
(222, '2023-01-01', '2023-01-01', 'iamge 222 1', true, 227),
(223, '2023-01-01', '2023-01-01', 'iamge 222 2', true, 227)
;

insert into post_manager_mapper (id,created_at , modified_at ,is_fixed, is_posted,post_id)values
(111, '2023-01-01','2023-01-01',  true, true, 224 ),
(112, '2023-01-01','2023-01-01',  false, true, 225),
(113, '2023-01-01','2023-01-01',  false, true, 226),
(114, '2023-01-01','2023-01-01',  false, true, 227),
(115, '2023-01-01','2023-01-01',  false, true, 228),
(116, '2023-01-01','2023-01-01',  false, true, 229),
(117, '2023-01-01','2023-01-01',  false, true, 230),
(118, '2023-01-01','2023-01-01',  false, true, 231),
(119, '2023-01-01','2023-01-01',  false, true, 232),
(120, '2023-01-01','2023-01-01',  false, true, 233),
(121, '2023-01-01','2023-01-01',  false, true, 234),
(122, '2023-01-01','2023-01-01',  false, true, 235),
(123, '2023-01-01','2023-01-01',  false, true, 236),
(124, '2023-01-01','2023-01-01',  false, true, 237),
(125, '2023-01-01','2023-01-01',  false, true, 238),
(126, '2023-01-01','2023-01-01',  false, true, 239),
(127, '2023-01-01','2023-01-01',  false, true, 240)
;
insert into carousel_manager (id,created_at,modified_at,banner_image,carousel_order,carousel_type,carouseled,target_id) values
(111,  '2023-01-01','2023-01-01',  'image', 1,'EVENT', true, 111 ),
(112,  '2023-01-01','2023-01-01',  'image2', 0,'EVENT', false, 121 ),
(113,  '2023-01-01','2023-01-01',  'image3', 0,'EVENT', false, 112 ),
(114,  '2023-01-01','2023-01-01',  'image3', 0,'EVENT', false, 116 );