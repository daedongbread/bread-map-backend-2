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

TRUNCATE TABLE bakery_view;



insert into USER (id, created_at, modified_at, role_type, is_block, is_marketing_info_reception_agreed, is_alarm_on, oauth_type, oauth_id, nick_name, email, gender, image)values
(111,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_111', 'nick_name', 'test@apple.com' , 'MALE', 'image'),
(112,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_222', 'nick_name222', 'test1@apple.com' , 'MALE', 'image'),
(113,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_333', 'nick_name333', 'test2@apple.com' , 'MALE', 'image'),
(114,  '2023-01-01', '2023-01-01', 'USER', false,  true, false, 'APPLE', 'APPLE_444', 'nick_name444', 'test3@apple.com' , 'MALE', 'image')
;


insert into FLAG (id, created_at, modified_at, color, name, user_id )values
(100,  '2023-01-01', '2023-01-01', 'YELLOW', '가고싶어요', 111),
(111,  '2023-01-01', '2023-01-01', 'YELLOW', '가즈아', 111),
(112,  '2023-01-02', '2023-01-01', 'CYAN', 'flag1', 111),
(113,  '2023-01-03', '2023-01-01', 'PINK', 'flag2', 112)
;

insert into FLAG_BAKERY (id, created_at, modified_at, bakery_id, flag_id, user_id )values
(111,  '2023-01-01', '2023-01-01', 100, 111, 111),
(112,  '2023-01-02', '2023-01-01', 200, 112, 111),
(113,  '2023-02-01', '2023-01-01', 600, 112, 111),
(114,  '2023-01-03', '2023-01-01', 300, 113, 112),
(115,  '2023-01-03', '2023-01-01', 400, 113, 112),
(116,  '2023-01-03', '2023-01-01', 500, 113, 112)
;

INSERT INTO bakery_add_report (id, created_at, modified_at, content, location, name, status, user_id) VALUES
(111, '2023-04-28 22:10:05', '2023-05-01 00:41:43', 'Shxjx', 'Xbxjx', 'Djdjx', 'NOT_REFLECT', 111),
(211, '2023-04-29 12:49:56', '2023-05-01 00:41:50', '', '강남역', '테스트트트', 'NOT_REFLECT', 114),
(311, '2023-04-30 18:37:41', '2023-05-01 16:48:02', '', '대전 중구 대종로480번길 15 (우)34921', '성심당 본점', 'REFLECT', 112),
(411, '2023-04-30 18:38:37', '2023-05-01 16:57:36', '', '대전 중구 대종로 480 1층 (우)34921', '성심당 케익부띠끄', 'REFLECT', 113)
;

insert into bakery (report_id, id, created_at, modified_at, address, blogurl, facebookurl, instagramurl, websiteurl, facility_info_list, hours, image, latitude, longitude, name, phone_number, status,  owner_id, detailed_address)values
(111, 100, '2023-01-12 15:00:00', '2023-04-05 15:00:00', '서울특별시 중랑구 동일로 778', null, null, null, null, null, '정보 없음', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery4.png', 37.5999492, 127.08009, '케이크하우스밀레 중화점', '02-433-8039', 'POSTING',  null, null),
(211, 200, '2023-03-13 15:00:00', '2023-04-05 15:00:00', '서울 중랑구 면목로44라길 20 1층 빵꽃빵꽃', null, null, 'https://www.instagram.com/bbangkkot_bakery', null, 'DELIVERY,WIFI,PARKING,BOOKING', '-매주 월요일, 화요일 정기휴무 매일 11:00 - 18:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery4.png', 37.5818039, 127.09167, '빵꽃빵꽃', '0507-1372-1808', 'POSTING', null, null),
(311, 300, '2023-05-22 15:00:00', '2023-04-05 15:00:00', '서울특별시 송파구 송파대로 111, 114호', null, null, null, null, 'WIFI,PARKING', '매일 09:00 - 22:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.4796867, 127.124978, '외계인방앗간 문정점', '02-430-5989', 'POSTING',  null, null),
(411, 500, '2023-02-01 15:00:00', '2023-04-05 15:00:00', '서울특별시 중랑구 동일로163길 37, 홍주빌딩 1층 1호', null, null, null, null, null, '매일 09:00 - 22:00 -매주 일요일 정기휴무', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery8.png', 37.611713, 127.075079, '정수제빵소', '02-949-5906', 'POSTING',  null, null),
(null, 600, '2023-03-25 15:00:00', '2023-04-05 15:00:00', '서울 강동구 동남로81길 88', null, null, 'https://www.instagram.com/forefore_/', null, 'PET,BOOKING', '월 정기휴무 (매주 월요일) 화 정기휴무 (매주 화요일)수 13:00 - 19:00 목 13:00 - 19:00 금 13:00 - 19:00 토 13:00 - 17:00 일 정기휴무 (매주 일요일)', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery8.png', 37.558956, 127.15226, '포레포레', '0507-1327-1226', 'POSTING',  null, null),
(null, 700, '2023-01-21 15:00:00', '2023-04-05 15:00:00', '서울특별시 은평구 은평로3길 21 1층', null, null, null, null, 'PARKING', '월 09:00 - 21:00화 09:00 - 21:00 수 09:00 - 21:00 목 09:00 - 21:00 금 09:00 - 21:00 토 09:00 - 18:00 일 정기휴무 (매주 일요일) - 마지막주 토요일 휴무', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.6008447, 126.914991, '브레드82', '02-356-6882', 'POSTING', null, null),
(null, 800, '2023-06-09 15:00:00', '2023-04-05 15:00:00', '서울특별시 관악구 난곡로43길 12, 1층 2호', null, null, 'http://instagram.com/seoradang', null, 'WIFI,PARKING', '월 정기휴무화 정기휴무수 정기휴무목 14:00 - 21:00금 14:00 - 21:00토 14:00 - 21:00일 14:00 - 21:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery6.png', 37.4759769, 126.915265, '서라당', '0507-1323-9638', 'POSTING',  null, null),
(null, 900, '2023-07-08 15:00:00', '2023-04-05 15:00:00', '서울특별시 성동구 왕십리로 410, E동 104호', null, null, null, null, null, '월 정기휴무 (매주 월요일)화 10:00 - 22:00수 10:00 - 22:00목 10:00 - 22:00금 10:00 - 22:00토 10:00 - 22:00일 10:00 - 22:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery8.png', 37.5666027, 127.024083, '몽쥬 빠티세리(Monge patisserie)', '02-6448-3241', 'POSTING', null, null),
(null, 1000, '2023-01-02 15:00:00', '2023-04-05 15:00:00', '서울특별시 강남구 언주로 314, 강남프라자 1층 108호', null, null, 'https://www.instagram.com/mochimochi_gangnam/', null, 'PARKING', '매일 08:00 - 20:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.495879, 127.046483, '모찌모찌브레드 역삼점', '0507-1329-8070', 'POSTING', null, null),
(null, 1100, '2023-02-27 15:00:00', '2023-04-05 15:00:00', '서울특별시 서대문구 연희맛로 7-29, 지하1층', null, null, 'http://www.instagram.com/lesoleil_official', null, 'WIFI', '월 정기휴무화 정기휴무수 12:00 - 19:00목 12:00 - 19:00금 12:00 - 19:00토 12:00 - 19:00일 12:00 - 19:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery5.png', 37.5663689, 126.927985, '르솔레이', '0507-1315-6371', 'POSTING', null, null),
(null, 1200, '2023-02-16 15:00:00', '2023-04-05 15:00:00', '서울특별시 마포구 창전로 45, 101동 1층 109호', null, null, null, null, null, '매일 11:00 - 19:00', 'https://d2a72lvyl71dvx.cloudfront.net/defaultImage/defaultBakery3.png', 37.5462603, 126.931899, '파이앤브라우니', '0507-1416-3132', 'POSTING', null, null)
;

INSERT INTO follow (id, created_at, modified_at, from_user_id, to_user_id) VALUES
(1, '2023-05-01 15:39:30', '2023-05-01 15:39:30', 111, 112),
(2, '2023-05-02 10:13:19', '2023-05-02 10:13:19', 112, 113),
(3, '2023-05-02 12:16:30', '2023-05-02 12:16:30', 112, 111);



