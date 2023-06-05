package com.depromeet.breadmapbackend.domain.notice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomFirebaseProperties;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
	private final NoticeTokenRepository noticeTokenRepository;
	private final CustomFirebaseProperties customFirebaseProperties;

	public void sendMessageTo(NoticeFcmDto dto) throws FirebaseMessagingException {

		List<String> tokens = noticeTokenRepository.findByUser(dto.getUserId())
			.stream().map(NoticeToken::getDeviceToken).collect(Collectors.toList());
		if (tokens.isEmpty())
			return;

		MulticastMessage message = MulticastMessage.builder()
			.setNotification(new Notification(dto.getTitle(), dto.getContent()))
			.putData("path", makePath(dto.getContentId(), dto.getType()))
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
		if (type.equals(NoticeType.FOLLOW))
			return customFirebaseProperties.getMessage().getPath().getUser() + contentId;
		else if (type.equals(NoticeType.REVIEW_COMMENT) || type.equals(NoticeType.REVIEW_LIKE))
			return customFirebaseProperties.getMessage().getPath().getReview() + contentId;
		else if (type.equals(NoticeType.RECOMMENT) || type.equals(NoticeType.REVIEW_COMMENT_LIKE))
			return ""; // TODO : 댓글 엔드포인트
			//        else if(type.equals(NoticeType.ADD_BAKERY) || type.equals(NoticeType.ADD_PRODUCT))
			//            return "";
			//        else if(type.equals(NoticeType.FLAG_BAKERY_CHANGE) || type.equals(NoticeType.FLAG_BAKERY_ADMIN_NOTICE))
			//            return "";
		else
			throw new DaedongException(DaedongStatus.NOTICE_TYPE_EXCEPTION);
	}
}