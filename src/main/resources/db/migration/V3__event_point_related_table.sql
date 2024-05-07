CREATE TABLE `bread_diary_event` (
                                           `diary_id` bigint PRIMARY KEY ,
                                           `state` ENUM ('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
                                           `point` int NOT NULL DEFAULT 0,
                                           `description` string NOT NULL DEFAULT '',
                                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `point_history` (
                                 `id` bigint auto_increment PRIMARY KEY,
                                 `user_id` bigint NOT NULL,
                                 `target_id` bigint,
                                 `point` int NOT NULL DEFAULT 0,
                                 `grand_total_point` int NOT NULL DEFAULT 0,
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `type` ENUM ('BREAD_DIARY', 'ETC') NOT NULL DEFAULT 'ETC',
                                 `description` string NOT NULL DEFAULT ''
);

CREATE TABLE `user_point` (
                              `user_id` bigint PRIMARY KEY,
                              `total_point` int NOT NULL DEFAULT 0,
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE `bread_diary_event` ADD FOREIGN KEY (`diary_id`) REFERENCES `bread_diary` (`id`);

ALTER TABLE `point_history` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `user_point` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
