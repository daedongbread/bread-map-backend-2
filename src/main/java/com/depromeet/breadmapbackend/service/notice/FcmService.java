package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.infra.properties.CustomFirebaseProperties;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
/*
https://koharinn.tistory.com/588
https://velog.io/@co323co/SpringBoot-Push-Server#%EC%97%AC%EB%9F%AC%EB%AA%85-%EA%B8%B0%EA%B8%B0%EA%B7%B8%EB%A3%B9
https://velog.io/@2yeseul/Spring-Boot%EC%97%90-FCM-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0
https://backtony.github.io/spring/2021-08-20-spring-fcm-1/#step-1---firebase-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%A7%8C%EB%93%A4%EA%B8%B0
https://kerobero.tistory.com/38
==
https://team-platform.tistory.com/23

 */
public class FcmService {
    private final NoticeTokenRepository noticeTokenRepository;
    private final CustomFirebaseProperties customFirebaseProperties;

    public void sendMessageTo(User user, String title, String content, Long contentId, NoticeType type) throws FirebaseMessagingException {
        if(!user.getIsAlarmOn()) return;

        List<String> tokens = noticeTokenRepository.findByUser(user)
                .stream().map(NoticeToken::getDeviceToken).collect(Collectors.toList());

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(new Notification(title, content))
                .putData("path", makePath(contentId, type))
                .addAllTokens(tokens)
                .build();
        try {
            ApiFuture<BatchResponse> response = FirebaseMessaging.getInstance().sendMulticastAsync(message);
//            response.addListener(); TODO
            ApiFutures.addCallback(response, new ApiFutureCallback<BatchResponse>() {
                @Override
                public void onFailure(Throwable t) {
                    log.error("FCM error : " + t.getMessage());
                }

                @Override
                public void onSuccess(BatchResponse result) {
                    if (result.getFailureCount() > 0) {
                        List<SendResponse> responses = result.getResponses();
                        for (int i = 0; i < responses.size(); i++) {
                            if (!responses.get(i).isSuccessful()) {
                                // The order of responses corresponds to the order of the registration tokens.
                                noticeTokenRepository.deleteByDeviceToken(tokens.get(i));
                            }
                        }
                    }
                }
            }, Runnable::run);
        } catch (Exception e) {
            log.error("Firebase FirebaseMessagingException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String makePath(Long contentId, NoticeType type) {
        if(type.equals(NoticeType.FOLLOW)) return customFirebaseProperties.getMessage().getPath().getUser() + contentId;
        else if(type.equals(NoticeType.REVIEW_COMMENT) || type.equals(NoticeType.REVIEW_LIKE))
            return customFirebaseProperties.getMessage().getPath().getReview() + contentId;
        else if(type.equals(NoticeType.RECOMMENT) || type.equals(NoticeType.REVIEW_COMMENT_LIKE))
            return ""; // TODO : 댓글 엔드포인트
//        else if(type.equals(NoticeType.ADD_BAKERY) || type.equals(NoticeType.ADD_PRODUCT))
//            return "";
//        else if(type.equals(NoticeType.FLAG_BAKERY_CHANGE) || type.equals(NoticeType.FLAG_BAKERY_ADMIN_NOTICE))
//            return "";
        else throw new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION);
    }
}