package com.depromeet.breadmapbackend.domain.admin.bakery;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.search.dto.UpdateBakeryMessage;
import com.depromeet.breadmapbackend.global.SQSService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateBakerySQSService {
	private final SQSService sqsService;
	private final String UPDATE_BAKERY_QUEUE_NAME = "UpdateBakeryQueue.fifo"; // TODO : go to config

	public void sendMessage(UpdateBakeryMessage message) {
		sqsService.sendMessage(UPDATE_BAKERY_QUEUE_NAME, message, message.getBakeryId());
	}
}
