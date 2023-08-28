create table bakery_images (
    bakery_id bigint not null,
    images varchar(255),
    foreign key (bakery_id) references bakery (id)
);

insert into bakery_images (bakery_id, images) select id, image from bakery;

alter table bakery drop column image;



