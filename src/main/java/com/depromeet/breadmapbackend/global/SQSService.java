package com.depromeet.breadmapbackend.global;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.global.dto.SQSDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SQSService {
	private final QueueMessagingTemplate queueMessagingTemplate;
	public void sendMessageToFIFOQueue(String queueName, SQSDto sqsDto, Long entityId) {
		Map<String, Object> header = Map.of(
			SqsMessageHeaders.SQS_GROUP_ID_HEADER, entityId,
			SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, UUID.randomUUID().toString()
		);
		queueMessagingTemplate.convertAndSend(queueName, sqsDto, header);
	}
}
