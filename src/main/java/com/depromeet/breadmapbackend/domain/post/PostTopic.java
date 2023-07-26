package com.depromeet.breadmapbackend.domain.post;

import java.util.Arrays;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostTopic {
	ALL("all"),
	REVIEW("review"),
	EVENT("event"),
	BREAD_STORY("bread_story"),
	FREE_TALK("free_talk"),
	;

	private final String topic;

	public static PostTopic of(final String topic) {
		return Arrays.stream(PostTopic.values())
			.filter(postTopic -> postTopic.getTopic().equals(topic))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_POST_TOPIC));
	}
}
