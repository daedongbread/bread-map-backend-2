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

TRUNCATE TABLE admin;
ALTER TABLE admin
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE bakery_add_report;
ALTER TABLE bakery_add_report
    ALTER COLUMN ID RESTART WITH 1;


TRUNCATE TABLE REVIEW_PRODUCT_RATING;
ALTER TABLE REVIEW_PRODUCT_RATING
    ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE product;
ALTER TABLE product
    ALTER COLUMN ID RESTART WITH 1;