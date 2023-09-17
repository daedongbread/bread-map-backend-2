package com.depromeet.breadmapbackend.domain.search;

import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.search.dto.UpdateBakeryMessage;

@Service
public class IndexListener {
	private final String UPDATE_BAKERY_QUEUE_NAME = "UpdateBakeryQueue.fifo"; // TODO : go to config

	@SqsListener(value = UPDATE_BAKERY_QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void listen(@Payload UpdateBakeryMessage message, Acknowledgment ack) {
		// update index of Opensearch
		ack.acknowledge();
	}
}
