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

TRUNCATE TABLE follow;
ALTER TABLE follow
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE bakery_add_report;
ALTER TABLE bakery_add_report
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE scored_bakery;
ALTER TABLE scored_bakery
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE admin;
ALTER TABLE admin
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE bakery_view;


insert into admin (id,created_at,modified_at,email,password,role_type) values
    (111,  '2023-01-01', '2023-01-01', 'admin@email.com', 'test-password', 'ADMIN' )
;

insert into USER (id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test1@apple.com' , 'MALE', 'image'),
(113,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_333', 'nick_name333', 'test2@apple.com' , 'MALE', 'image'),
(114,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_444', 'nick_name444', 'test3@apple.com' , 'MALE', 'image')
;



insert into bakery (id, created_at, modified_at, address, blogurl, facebookurl, instagramurl, websiteurl, facility_info_list, hours, image, latitude, longitude, name, phone_number, status, owner_id, detailed_address)values
(100, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 중랑구 동일로 778', null, null, null, null, null, '정보 없음', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery4.png', 37.5999492, 127.08009, '케이크하우스밀레 중화점', '02-433-8039', 'POSTING', null, null),
(200, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울 중랑구 면목로44라길 20 1층 빵꽃빵꽃', null, null, 'https://www.instagram.com/bbangkkot_bakery', null, 'DELIVERY,WIFI,PARKING,BOOKING', '-매주 월요일, 화요일 정기휴무 매일 11:00 - 18:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery4.png', 37.5818039, 127.09167, '빵꽃빵꽃', '0507-1372-1808', 'POSTING', null, null),
(300, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 송파구 송파대로 111, 114호', null, null, null, null, 'WIFI,PARKING', '매일 09:00 - 22:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.4796867, 127.124978, '외계인방앗간 문정점', '02-430-5989', 'POSTING', null, null),
(500, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 중랑구 동일로163길 37, 홍주빌딩 1층 1호', null, null, null, null, null, '매일 09:00 - 22:00 -매주 일요일 정기휴무', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery8.png', 37.611713, 127.075079, '정수제빵소', '02-949-5906', 'POSTING', null, null),
(600, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울 강동구 동남로81길 88', null, null, 'https://www.instagram.com/forefore_/', null, 'PET,BOOKING', '월 정기휴무 (매주 월요일) 화 정기휴무 (매주 화요일)수 13:00 - 19:00 목 13:00 - 19:00 금 13:00 - 19:00 토 13:00 - 17:00 일 정기휴무 (매주 일요일)', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery8.png', 37.558956, 127.15226, '포레포레', '0507-1327-1226', 'POSTING', null, null),
(700, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 은평구 은평로3길 21 1층', null, null, null, null, 'PARKING', '월 09:00 - 21:00화 09:00 - 21:00 수 09:00 - 21:00 목 09:00 - 21:00 금 09:00 - 21:00 토 09:00 - 18:00 일 정기휴무 (매주 일요일) - 마지막주 토요일 휴무', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.6008447, 126.914991, '브레드82', '02-356-6882', 'POSTING', null, null),
(800, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 관악구 난곡로43길 12, 1층 2호', null, null, 'http://instagram.com/seoradang', null, 'WIFI,PARKING', '월 정기휴무화 정기휴무수 정기휴무목 14:00 - 21:00금 14:00 - 21:00토 14:00 - 21:00일 14:00 - 21:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery6.png', 37.4759769, 126.915265, '서라당', '0507-1323-9638', 'POSTING', null, null),
(900, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 성동구 왕십리로 410, E동 104호', null, null, null, null, null, '월 정기휴무 (매주 월요일)화 10:00 - 22:00수 10:00 - 22:00목 10:00 - 22:00금 10:00 - 22:00토 10:00 - 22:00일 10:00 - 22:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery8.png', 37.5666027, 127.024083, '몽쥬 빠티세리(Monge patisserie)', '02-6448-3241', 'POSTING', null, null),
(1000, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 강남구 언주로 314, 강남프라자 1층 108호', null, null, 'https://www.instagram.com/mochimochi_gangnam/', null, 'PARKING', '매일 08:00 - 20:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.495879, 127.046483, '모찌모찌브레드 역삼점', '0507-1329-8070', 'POSTING', null, null),
(1100, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 서대문구 연희맛로 7-29, 지하1층', null, null, 'http://www.instagram.com/lesoleil_official', null, 'WIFI', '월 정기휴무화 정기휴무수 12:00 - 19:00목 12:00 - 19:00금 12:00 - 19:00토 12:00 - 19:00일 12:00 - 19:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery5.png', 37.5663689, 126.927985, '르솔레이', '0507-1315-6371', 'POSTING', null, null),
(1200, '2023-04-05 15:00:00', '2023-04-05 15:00:00', '서울특별시 마포구 창전로 45, 101동 1층 109호', null, null, null, null, null, '매일 11:00 - 19:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.5462603, 126.931899, '파이앤브라우니', '0507-1416-3132', 'POSTING', null, null)
;

insert into scored_bakery (id, calculated_date, rating, flag_count,bakery_rank,total_score,view_count,bakery_id) values
(111, '2023-07-07',4.1, 10, 1,110,100, 100),
(112, '2023-07-07',4.1, 9, 2,109,100, 200),
(113, '2023-07-07',4.1, 8, 3,108,100, 300),
(114, '2023-07-07',4.1, 7, 4,107,100, 400),
(115, '2023-07-07',4.1, 6, 5,106,100, 500),

(116, '2023-07-08',4.1, 10, 1,110,100, 100),
(117, '2023-07-08',4.1, 9, 2,109,100, 200),
(118, '2023-07-08',4.1, 8, 3,108,100, 300),
(119, '2023-07-08',4.1, 7, 4,107,100, 400),
(120, '2023-07-08',4.1, 6, 5,106,100, 500),

(121, '2023-07-09',4.3, 10, 1,110,100, 100),
(122, '2023-07-09',4.3, 9, 2,109,100, 200),
(123, '2023-07-09',4.3, 8, 3,108,100, 300),
(124, '2023-07-09',4.3, 7, 4,107,100, 400),
(125, '2023-07-09',4.3, 6, 5,106,100, 500),

(126, '2023-07-10', 3.7, 10, 1,110,100, 100),
(127, '2023-07-10', 3.7, 9, 2,109,100, 200),
(128, '2023-07-10', 3.7, 8, 3,108,100, 300),
(129, '2023-07-10', 3.7, 7, 4,107,100, 400),
(130, '2023-07-10', 3.7, 6, 5,106,100, 500),

(131, '2023-07-11',1.5, 10, 1,110,100, 100),
(132, '2023-07-11',1.5, 9, 2,109,100, 200),
(133, '2023-07-11',1.5, 8, 3,108,100, 300),
(134, '2023-07-11',1.5, 7, 4,107,100, 400),
(135, '2023-07-11',1.5, 6, 5,106,100, 500),

(136, '2023-07-20', 2.5, 10, 1,110,100, 100),
(137, '2023-07-20', 2.5, 9, 2,109,100, 200),
(138, '2023-07-20', 2.5, 8, 3,108,100, 300),
(139, '2023-07-20', 2.5, 7, 4,107,100, 400),
(140, '2023-07-20', 2.5, 6, 5,106,100, 500),

(141, '2023-07-13', 2.7, 10, 1,110,100, 100),
(142, '2023-07-13', 2.7, 9, 2,109,100, 200),
(143, '2023-07-13', 2.7, 8, 3,108,100, 300),
(144, '2023-07-13', 2.7, 7, 4,107,100, 400),
(145, '2023-07-13', 2.7, 6, 5,106,100, 500),

(146, '2023-07-14', 1.9, 10, 1,110,100, 100),
(147, '2023-07-14', 1.9, 9, 2,109,100, 200),
(148, '2023-07-14', 1.9, 8, 3,108,100, 300),
(149, '2023-07-14', 1.9, 7, 4,107,100, 400),
(150, '2023-07-14', 1.9, 6, 5,106,100, 500)
;