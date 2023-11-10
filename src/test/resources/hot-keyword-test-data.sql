SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE admin;
ALTER TABLE admin
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE HOT_KEYWORD;
ALTER TABLE HOT_KEYWORD
    ALTER COLUMN ID RESTART WITH 1;

insert into hot_keyword( id, created_at, modified_at,  keyword, rank ) values
(999, '2023-05-02 12:16:30', '2023-05-02 12:16:30', '소금빵', 1),
(998, '2023-05-02 12:16:30', '2023-05-02 12:16:30',  '붕어빵', 2),
(997, '2023-05-02 12:16:30', '2023-05-02 12:16:30',   '빵빵빵', 3)
;

insert into admin (id,created_at,modified_at,email,password,role_type) values
(111,  '2023-01-01', '2023-01-01', 'admin@email.com', 'test-password', 'ADMIN' )
;
