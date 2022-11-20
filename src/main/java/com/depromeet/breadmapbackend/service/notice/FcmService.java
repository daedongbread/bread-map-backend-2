package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeTokenNotFoundException;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.web.controller.notice.dto.FcmMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
/*
https://koharinn.tistory.com/588
https://velog.io/@co323co/SpringBoot-Push-Server#%EC%97%AC%EB%9F%AC%EB%AA%85-%EA%B8%B0%EA%B8%B0%EA%B7%B8%EB%A3%B9
https://velog.io/@2yeseul/Spring-Boot%EC%97%90-FCM-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0
https://backtony.github.io/spring/2021-08-20-spring-fcm-1/#step-1---firebase-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%A7%8C%EB%93%A4%EA%B8%B0
https://kerobero.tistory.com/38
 */
public class FcmService {
    private final NoticeTokenRepository noticeTokenRepository;

    public void sendMessageTo(String deviceToken, String title, String body, String path) throws FirebaseMessagingException {
        NoticeToken noticeToken = noticeTokenRepository.findByDeviceToken(deviceToken)
                .orElseThrow(NoticeTokenNotFoundException::new);
        if(!noticeToken.isAlarmOn()) return;

        // TODO 안드로이드 IOS 구분 필요?? 관리자는 Web?
        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(new Notification(title, body))
                .putData("path", path)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }


//    public void sendMessageTo(String deviceToken, String title, String body, String path) throws Exception {
//        NoticeToken noticeToken = noticeTokenRepository.findByDeviceToken(deviceToken).orElseThrow(NoticeTokenNotFoundException::new);
//        if(!noticeToken.isAlarmOn()) return;
//        // TODO : deviceToken 만료시??
//
//        String message = makeMessage(deviceToken, title, body, path);
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), message);
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(requestBody)
//                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
//                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
//                .build();
//
//        client.newCall(request).execute();
////        Response response  = client.newCall(request).execute();
////        log.info(response.body().string());
//    }

//    private String makeMessage(String deviceToken, String title, String body, String path) throws JsonProcessingException {
//        FcmMessage fcmMessage = FcmMessage.builder()
//                .message(FcmMessage.Message.builder()
//                        .token(deviceToken)
//                        .notification(FcmMessage.Notification.builder()
//                                .title(title)
//                                .body(body)
//                                .build()
//                        )
//                        .data(FcmMessage.FcmData.builder()
//                                .path(path)
//                                .build()
//                        )
//                        .build()
//                ).build();
//
////        log.info(objectMapper.writeValueAsString(fcmMessage));
//        return objectMapper.writeValueAsString(fcmMessage);
//    }

//    private String getAccessToken() throws Exception {
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(List.of(firebaseScope));
//
//        // accessToken 생성
//        googleCredentials.refreshIfExpired();
//
//        // GoogleCredential의 getAccessToken으로 토큰 받아온 뒤, getTokenValue로 최종적으로 받음
//        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
//        return googleCredentials.getAccessToken().getTokenValue();
//    }


}