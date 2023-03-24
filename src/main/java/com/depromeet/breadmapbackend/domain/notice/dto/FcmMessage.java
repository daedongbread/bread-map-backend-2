package com.depromeet.breadmapbackend.domain.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmMessage {
    private Message message;

    @Getter
    @NoArgsConstructor
    public static class Message {
        private String token;
        private Notification notification; // 알람의 제목과 내용
        private FcmData data; // 알람 클릭 시 이동 정보

        @Builder
        public Message(String token, Notification notification, FcmData data) {
            this.token = token;
            this.notification = notification;
            this.data = data;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Notification {
        private String title;
        private String body;
//        private String image;

        @Builder
        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class FcmData {
        private String path;

        @Builder
        public FcmData(String path) {
            this.path = path;
        }
    }

    @Builder
    public FcmMessage(Message message) {
        this.message = message;
    }
}