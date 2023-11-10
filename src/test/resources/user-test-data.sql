SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE USER;
ALTER TABLE USER ALTER COLUMN ID RESTART WITH 1;

insert into USER (is_de_registered,id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(false, 111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'GOOGLE', 'TEST_111', 'nick_name', 'admin@email.com' , 'MALE', 'image'),
(false, 112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'GOOGLE', 'TEST_222', 'nick_name222', 'test2@TEST.com' , 'MALE', 'image'),
(false, 113,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'GOOGLE', 'TEST_333', 'nick_name333', 'tes3t@TEST.com' , 'MALE', 'image')
;
