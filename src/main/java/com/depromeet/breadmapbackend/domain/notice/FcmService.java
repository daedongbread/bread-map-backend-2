package com.depromeet.breadmapbackend.domain.notice;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

	public void sendMessageTo(NoticeFcmDto dto) throws FirebaseMessagingException {
		try {
			FirebaseMessaging.getInstance().sendMulticastAsync(
				MulticastMessage.builder()
					.setNotification(new Notification(dto.getTitle(), dto.getContent()))
					.putAllData(
						Map.of(
							"contentId", dto.getContentId().toString(),
							"subContentId", dto.getSubContentId() != null ? dto.getSubContentId().toString() : "",
							"type", dto.getType().toString(),
							"extraParam", dto.getExtraParam() != null ? dto.getExtraParam() : ""
						)
					)
					.addAllTokens(dto.getFcmTokens())
					.setAndroidConfig(
						AndroidConfig.builder()
							.setPriority(AndroidConfig.Priority.HIGH)
							.setNotification(
								AndroidNotification.builder()
									.setSound("default")
									.setChannelId("com.daedongbread")
									.build()
							)
							.build()
					)
					.build()
			);
		} catch (Exception e) {
			log.error("Firebase FirebaseMessagingException : " + e.getMessage() + " " + dto.toString());
		}
	}
}
