create table challenge_participant
(
    id           bigint auto_increment not null,
    created_at   datetime null,
    modified_at  datetime null,
    challenge_id bigint not null,
    user_id      bigint not null,
    constraint pk_challengeparticipant primary key (id)
);

alter table challenge_participant
    add constraint fk_challengeparticipant_on_challenge foreign key (challenge_id) references challenge (id);

alter table challenge_participant
    add constraint fk_challengeparticipant_on_user foreign key (user_id) references user (id);